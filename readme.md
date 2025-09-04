# Systemvoraussetzungen

Die Aufgaben wurden auf einem Windowsrechner mit den folgenden Javaversionen programmiert und getestet.

```
$ java --version
openjdk 24.0.1 2025-04-15
OpenJDK Runtime Environment (build 24.0.1+11)
OpenJDK 64-Bit Server VM (build 24.0.1+11, mixed mode, sharing)
```


# Starten der Anwendung

```
cd project
./gradlew.bat bootRun --args="input.txt"
```



# Durchführen der Tests

In der MainClassTests.java sind Tests für die komplizierteste Methode geschrieben.
Es wird dort getestet, ob die Linien korrekt zusammmengefügt werden.


```
cd project
./gradlew.bat test
```

## Durchführen der Tests mit build command

```
cd project
./gradlew.bat build
```


# Besonderheiten

## Chain / Linienzug

Die einzelnen Line-Objekte des Linienzugs sind im Chain-Objekt nicht sortiert. 
(angrenzende Liniensegmente folgen nicht unbedingt aufeinander)

### Linienzug Algorithmus

Zuerst werden alle Punkte zu ihren anliegenden Linien gemappt.

Danach werden alle Punkte mit genau 2 Linien betrachtet, für diese Punkte gibt es 3 Möglichkeiten:
- beide Linien sind in einem Linienzug --> die beiden Linienzüge werden durch den betrachteten Punkt verbunden
- keine der beiden Linien ist bereits in einem Linienzug --> neuer Linienzug wird erstellt
- eine der Linien ist bereits Teil eines Linienzugs --> die andere Linie wird dem Linienzug hinzugefügt


