package com.takehome;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.imageio.ImageIO;

import com.takehome.pojos.Chain;
import com.takehome.pojos.Line;
import com.takehome.pojos.Point;

public class MainClass {
	public static void main(String[] args) throws IOException {
		if (args.length < 1){
			System.out.println("Please provide the data filename as first command line argument");
			System.exit(0);
		}

		String filename = args[0];

		System.out.println("Start reading from file " + filename);

		List<Line> lines = readLinesFromFile(filename);

		List<Chain> joinedLines = joinLines(lines);
		joinedLines.sort((o1, o2) -> Float.compare(o2.getLength(), o1.getLength()));


		System.out.println("All chains have been extracted");
		for (Chain chain : joinedLines) {
			System.out.println(chain.getLength() + " length chain: " + chain);
		}

		createImage(joinedLines);
	}


	static List<Line> readLinesFromFile(String filename) throws IOException {
		List<Line> lines = new ArrayList<Line>();
		BufferedReader reader = new BufferedReader(new FileReader(filename));

     	String currentLine = reader.readLine();
		int lineNumber = 0;
		while (currentLine!=null) {
			Optional<Line> line = extractLineObjectFromFileline(currentLine, lineNumber);
			
			if (line.isPresent()){
				lines.add(line.get());
			}

			currentLine = reader.readLine();
			lineNumber++;
		}
     	reader.close();

		return lines;
	}

	static Optional<Line> extractLineObjectFromFileline(String text, int lineNumber) {
		String[] parts = text.trim().split(" ");

		if (parts.length != 4){
			System.out.println("Unable to read point from line " + lineNumber);
			return Optional.empty();
		}

		float x1, y1, x2, y2;
		try {
			x1 = Float.parseFloat(parts[0]);
			y1 = Float.parseFloat(parts[1]);
			x2 = Float.parseFloat(parts[2]);
			y2 = Float.parseFloat(parts[3]);
		}
		catch (NumberFormatException e) {
			System.out.println("Unable to read point from line " + lineNumber);
			return Optional.empty();
		}

		Point p1 = new Point(x1, y1);
		Point p2 = new Point(x2, y2);

		return Optional.of(new Line(lineNumber, p1, p2, getDistance(p1, p2)));
	}

	static float getDistance(Point p1, Point p2) {
		return (float) Math.sqrt(Math.pow(p1.X() - p2.X(),2) + Math.pow(p1.Y() - p2.Y(), 2));
	}

	static List<Chain> joinLines(List<Line> lines) {
		// this function takes the lines and joins them at their overlapping points
		// a point to lines mapping is created

		HashMap<Point, List<Line>> pointLineMapping = new HashMap<Point, List<Line>>();

		for (Line line : lines) {
			for (Point p : new Point[]{line.p1(), line.p2()}){
				if (pointLineMapping.containsKey(p)==false) {
					// point is not yet in mapping --> create new list with only the line
					pointLineMapping.put(p, new ArrayList<Line>(Collections.singletonList(line)));
				} else if (pointLineMapping.containsKey(p)==true) {
					// point is already in mapping --> we add the line to the list
					List<Line> lines_of_point = pointLineMapping.get(p);
					lines_of_point.add(line);
					pointLineMapping.put(p, lines_of_point);
				}
			}
		}

		// now the points are mapped to all the lines they are a part of
		// next we check all mappings with exactly two lines and join them (resulting in a Chain Object)

		List<Chain> joins = new ArrayList<Chain>();

		int id_counter = 0;

		for (List<Line> linesTouchingPoint : pointLineMapping.values()) {
			// if exactly 2 lines touch a point they have to be joined and are part of a chain

			if (linesTouchingPoint.size() == 2) {
				Optional<Chain> leftOverlapChain = getOverlapChain(joins, linesTouchingPoint.get(0));
				Optional<Chain> rightOverlapChain = getOverlapChain(joins, linesTouchingPoint.get(1));
				
				if (leftOverlapChain.isPresent() && rightOverlapChain.isPresent()){
					// both lines that touch at a point overlap with an existing chain --> join the 2 chains together

					Set<Line> join_lines = new HashSet<Line>();
					join_lines.addAll(leftOverlapChain.get().elements());
					join_lines.addAll(rightOverlapChain.get().elements());
					Chain joined = new Chain(id_counter, join_lines);
					
					// add new chain, remoe old chains
					joins.add(joined);
					joins.removeIf(e -> e.id() == leftOverlapChain.get().id());//leftOverlapChain.get());
					joins.removeIf(e -> e.id() == rightOverlapChain.get().id());
					
					id_counter++;
				} else if (leftOverlapChain.isPresent()==false && rightOverlapChain.isPresent()==false) {
					// no overlap --> create new chain

					Chain chain = new Chain(id_counter, new HashSet<Line>(linesTouchingPoint));
					joins.add(chain);

					id_counter ++;
				} else {
					// one overlap
					// add missing line to chain

					if (leftOverlapChain.isPresent()){
						leftOverlapChain.get().addLineToChain(linesTouchingPoint.get(1));
					} else {
						rightOverlapChain.get().addLineToChain(linesTouchingPoint.get(0));
					}
				}
			}
		}

		return joins;
	}

	static Optional<Chain> getOverlapChain(List<Chain> joins, Line line){
		for (Chain chain : joins) {
			if (chain.lineOverlap(line)){
				return Optional.of(chain);
			}
		}
		return Optional.empty();
	}

	static void createImage(List<Chain> chains) throws IOException{
		int width = 1024;
		int height = 1024;

		BufferedImage buffImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

		Graphics2D g2d = buffImg.createGraphics();

		g2d.setColor(Color.white);
		g2d.fillRect(0, 0, width, height);

		Color[] colors = new Color[]{Color.black, Color.red, Color.green, Color.blue, Color.yellow, Color.MAGENTA};

		if (chains.size()>colors.length){
			System.out.println("There are not enough colors for the amount of chains to draw, please use a smaller file or change the sourcecode");
			System.exit(0);
		}

		for (int i = 0; i < chains.size();i++){
			Chain chain = chains.get(i);
			g2d.setColor(colors[i]);
			for (Line line : chain.elements()){
				g2d.drawLine((int) line.p1().X(),(int) line.p1().Y(), (int) line.p2().X(), (int) line.p2().Y());
			}
		}

		g2d.dispose();

		String filename = "star.png";
		File file = new File(filename);
		ImageIO.write(buffImg, "png", file);

		System.out.println("Image has been saved to " + filename);
	}
}