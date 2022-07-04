# Studien Design
## Ziel
**Das Tool sollte in der Lage sein, Verstöße anhand von REST API-Regeln zu erkennen.** Diese Regeln stammen von Masse und wurden durch die Forschung von Bogner und Kotstein validiert. 
**Durch die Erkennung von Regelverstößen soll das Tool Entwicklern helfen, bessere REST API Definitionen zu schreiben.** Bei der Implementierung der Regeln für die Erkennung sollten die Regeln mit hoher Wichtigkeit eine höhere Priorität erhalten und später die Regeln mit mittlerer Wichtigkeit implementiert werden. Die Priorität dieser Regeln, wurde durch die Forschung von Bogner und Kotstein bereits festgelegt [1].
**Das Tool wird zwischen dynamischer und statischer Analyse unterscheiden.** Um eine dynamische Analyse durchzuführen, wird das Tool die Eingaben des Benutzers nutzen, um dynamische die API aufrufe durchzuführen.
**Um die Genauigkeit des Tools zu messen, wird ein Benchmark mit mehr als 2k REST API Definitionen verwendet.**

### Forschungsfragen
**RQ1:** Ist das Tool in der Lage, die Einhaltung der **statischen** Regeln von Massé [2] zuverlässig zu erkennen?

**RQ2:** Ist das Tool in der Lage, die Einhaltung der **dynamischen** Regeln von Massé [2] zuverlässig zu erkennen?

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
* Einlesen einer json/yaml Datei durch das angeben eines Pfades
* Erkennen von Regelverstößen
* Report über die Regelverstöße mit Angabe in welcher Zeile Code er zu finden ist und wie kritisch er ist
* Report am Ende über die Softwarequalität (Regelverstöße sind auf Softwarequalität zurückzuführen)
* Output file (Regelverstöße + Verbesserungsvorschläge + Softwarequalität Report)

#### Warum wir ein neues Tool entwickeln
Existierende Tools sind zu **statisch**, schwer zu ergänzen (software quality att.; dynamische Regeln; evaluation der bisher impl. Regeln) --> Eigene Implementierung flexibler und vermutlich besserer/schnellere Weg für Anforderungen

### Schritt 3 (Evaluierung)
* 2.353 APIs von Apis.guru [3] minen und diese **statisch analysieren** (**RQ1**)(**automatisiert**) --> 

    1. Wie viele APIs kann das Tool problemlos untersuchen
        * Probleme beim Parsen wie auch bei der Analyse --> Wird ein Fehler geworfen?
    2. Was für statische Regelverstöße werden gefunden
    3. Vergleich mit anderen CLIs
        * Vergleich von Problemen beim Parsen wie auch bei der Analyse (1.)
        * Vergleich von Erkennung von selben Regeln (2.) --> Werden die selben Regeln an der selben Stelle von beiden CLIs erkannt?
* **Gold Standard** untersucht Genauigkeit --> Sammlung von in Schritt 2 erzeugten fehlerhaften openAPI Dateien, wobei alle Verstöße erkannt werden sollten (**RQ1; RQ2**)
* **Dynamische Analyse** von einzelnen APIs (**manuell**). Ähnlich wie bei der von der Studie von Palma et al. [4] (**RQ2**)
    * Was für dynamische Regelverstöße werden gefunden

# Roadmap
Datum | Meilenstein
------------- | -------------
06\. Juli    | Regeln analysieren (Schritt 1)
31\. August  | Regeln implementiert (Schritt 2)
15\. Oktober  | Evaluierung (Schritt 3)
30\. November | Paper schreiben



# Fragen
* Muss der gold standard (files) von dritten validiert werden?

# Quellen
[1] Kotstein, S., Bogner, J. (2021). Which RESTful API Design Rules Are Important and How Do They Improve Software Quality? A Delphi Study with Industry Experts. In: Barzen, J. (eds) Service-Oriented Computing. SummerSOC 2021. Communications in Computer and Information Science, vol 1429. Springer, Cham. https://doi.org/10.1007/978-3-030-87568-8_10

[2] https://www.oreilly.com/library/view/rest-api-design/9781449317904/

[3] https://apis.guru/

[4] Palma, F., Gonzalez-Huerta, J., Moha, N., Guéhéneuc, Y., & Tremblay, G. (2015). Are RESTful APIs Well-Designed? Detection of their Linguistic (Anti)Patterns. ICSOC. https://doi.org/10.1007/978-3-662-48616-0_11