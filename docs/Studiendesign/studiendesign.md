# Studien Design
## Ziel
**Das Tool sollte in der Lage sein, Verstöße anhand von REST API-Regeln zu erkennen.** Diese Regeln stammen von Masse und wurden durch die Forschung von Bogner und Kotstein validiert. 
**Durch die Erkennung von Regelverstößen soll das Tool Entwicklern helfen, bessere REST API Definitionen zu schreiben.** Bei der Implementierung der Regeln für die Erkennung sollten die Regeln mit hoher Wichtigkeit eine höhere Priorität erhalten und später die Regeln mit mittlerer Wichtigkeit implementiert werden. Die Priorität dieser Regeln, wurde durch die Forschung von Bogner und Kotstein bereits festgelegt [1].
**Das Tool wird zwischen dynamischer und statischer Analyse unterscheiden.** Um eine dynamische Analyse durchzuführen, wird das Tool die Eingaben des Benutzers nutzen, um dynamische die API aufrufe durchzuführen.
**Um die Genauigkeit des Tools zu messen, wird ein Benchmark mit mehr als 2k REST API Definitionen verwendet.**

### Forschungsfragen

**RQ1:** Ist das Tool in der Lage, die Einhaltung der Regeln von Massé [2] zuverlässig zu erkennen?

## Methoden
### Schritt 1 (Regeln analysieren)
* Regeln analysieren und kategorisieren (static/dynamic, notes und Ideen zum Impl.)
* Konzept aufstellen für die Analyse (md Datei auf GitHub)

### Schritt 2 (Regeln implementieren)
* Konzept für Regel erstellen (md Datei auf GitHub erstellen)
* Implementieren einer Regel
    * Zu jeder implementierten Regel werden von dem Entwickler Unit Tests geschrieben
    * Implementierter Code wird von zweitem Entwickler überprüft (PR)
    * Von zweitem und drittem Entwickler wird eine fehlerhafte openAPI Datei bereitgestellt (Gold Standard)
    * Wenn alles erkannt wurde und Softwarequalität passt --> Code in master pushen

#### Anforderungen an den CLI (Kann sich beim Entwickeln nochmal ändern)
* Reading of a json/yaml file by specifying a path
* Detection of rule violations
* Report about rule violations with indication in which line of code it can be found and how critical it is
* Report at the end about the software quality (rule violations are attributed/subordinated to quality properties)
* Output file (rule violations + improvement suggestions + software quality report)

#### Warum wir ein neues Tool entwickeln
Existierende Tools sind zu **statisch**, schwer zu ergänzen (software quality att.; dynamische Regeln; evaluation der bisher impl. Regeln) --> Eigene Implementierung flexibler und vermutlich besserer Weg für Anforderungen

### Schritt 3 (Evaluierung)
* 2.353 APIs von Apis.guru [3] minen --> 1. Wie viele APIs kann das Tool problemlos untersuchen 2. Was für statische rule violations werden erzeugt (Script automatisiert) 3. Vergleich mit anderen CLIs
* Dynamische Untersuchung von einzelnen APIs (Manuell). Ähnlich wie bei der vom Soda-Team durchgeführten Studie[4] 
* Gold Standard untersucht precision --> Sammlung von in Schritt 2 erzeugten fehlerhaften openAPI Dateien, wobei alle violations erkannt werden

# Roadmap
Datum | Meilenstein
------------- | -------------
06\. Juli    | Regeln analysieren (Schritt 1)
31\. August  | Regeln implementiert (Schritt 2)
15\. Oktober  | Evaluierung (Schritt 3)
30\. November | Paper schreiben



# Fragen


# Quellen
[1] https://link.springer.com/chapter/10.1007/978-3-030-87568-8_10

[2] https://www.oreilly.com/library/view/rest-api-design/9781449317904/

[3] https://apis.guru/

[4] http://sofa.uqam.ca/soda/