package com.takehome;

import org.junit.jupiter.api.Test;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import com.takehome.pojos.Chain;
import com.takehome.pojos.Line;
import com.takehome.pojos.Point;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.stream.Collectors;


@SpringJUnitConfig
public class MainClassTests {


    //@BeforeEach
    //public void setUp() {
    //    exceptionHandler = new GlobalExceptionHandler();
    //}

    Point a = new Point(0f, 00f);
    Point b = new Point(0f, 10f);
    Point c = new Point(10f,10f);
    Point d = new Point(10f, 0f);

    Point e = new Point(100f, 0f);
    Point f = new Point(100f, 10f);
    Point g = new Point(100f, 20f);

    Line ab = new Line(0, a, b, 10f);
    Line bc = new Line(1, b, c, 10f);
    Line bd = new Line(2, b, d, (float) Math.sqrt(200));
    Line cd = new Line(3, c, d, 10f);

    Line ef = new Line(4, e, f, 10f);
    Line fg = new Line(5, f, g, 10f);

    @Test
    public void testJoinLines_givenNoOverlappingLines_returnNoChains() throws Exception {
        // Act
        List<Chain> result = MainClass.joinLines(List.of(ab, cd));

        // Assert
        assertEquals(result.size(), 0);
    }

    @Test
    public void testJoinLines_givenTwoConnectingLines_returnChain() throws Exception {
        // Act
        List<Chain> result = MainClass.joinLines(List.of(ab, bc));

        // Assert
        assertEquals(result.size(), 1);
        Chain abc = result.get(0);

        assertEquals(abc.getLength(), ab.length() + bc.length());
        assertEquals(abc.elements().size(), 2);
        assertTrue(chainContainsLine(abc, ab));
        assertTrue(chainContainsLine(abc, bc));
    }

    @Test
    public void testJoinLines_givenThreeConnectingLines_returnChain() throws Exception {
        // Act
        List<Chain> result = MainClass.joinLines(List.of(ab, bc, cd));

        // Assert
        assertEquals(result.size(), 1);
        Chain abcd = result.get(0);

        assertEquals(abcd.getLength(), ab.length() + bc.length() + cd.length());
        assertEquals(abcd.elements().size(), 3);
        assertTrue(chainContainsLine(abcd, ab));
        assertTrue(chainContainsLine(abcd, bc));
        assertTrue(chainContainsLine(abcd, cd));
    }

    @Test
    public void testJoinLines_givenThreeLinesWithCommonPoint_returnNoChain() throws Exception {
        // Act
        List<Chain> result = MainClass.joinLines(List.of(ab, bc, bd));

        // Assert
        assertEquals(result.size(), 0);
    }

    @Test
    public void testJoinLines_givensFourLinesWithTwoCommonPoints_returnTwoChains() throws Exception {
        // Act
        List<Chain> result = MainClass.joinLines(List.of(ab, bc, ef, fg));

        // Assert
        assertEquals(result.size(), 2);
        Chain abc = result.get(0);
        Chain efg = result.get(1);

        assertEquals(abc.getLength(), ab.length() + bc.length());
        assertEquals(abc.elements().size(), 2);
        assertTrue(chainContainsLine(abc, ab));
        assertTrue(chainContainsLine(abc, bc));

        assertEquals(efg.getLength(), ef.length() + fg.length());
        assertEquals(efg.elements().size(), 2);
        assertTrue(chainContainsLine(efg, ef));
        assertTrue(chainContainsLine(efg, fg));
    }

    @Test
    public void testGetDistance_givenTwoIdenticalPoints_distanceZero() throws Exception {
        // Act
        float distance = MainClass.getDistance(a, a);

        // Assert
        assertEquals(distance, 0f);
    }

    @Test
    public void testGetDistance_givenTwoPoints_distanceCorrect() throws Exception {
        // Act
        float distance = MainClass.getDistance(a, b);

        // Assert
        assertEquals(distance, 10);
    }

    boolean chainContainsLine(Chain chain, Line line){
        for (Line ele : chain.elements()) {
            if (ele.id() == line.id()){
                return true;
            }
        }
        return false;
    }
}
