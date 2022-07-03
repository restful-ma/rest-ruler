# Statische und dynamische Analyse
Die in dem Buch von Massé [1] vorgestellten REST API design Regeln lassen sich statisch, wie auch dynamisch untersuchen. Für die Regeln, die statisch untersucht werden können, wird nur ein OpenAPI Dokument benötigt, welches dann analysiert werden kann. Hingegen bei den Regeln, die dynamisch untersucht werden müssen, muss man neben dem analysieren des OpenAPI Dokumentes auch die API aufrufe durchführen. Somit sind diese meist nicht voll automatisierbar und benötigen einen Input durch den Benutzer.

## Untersuchen von dynamischen Regeln
Um durch den CLI trotzdem dynamische Regeln bei einem OpenAPI Dokument zu untersuchen, wird beim Starten der Analyse der Benutzer gefragt, ob dieser auch die dynamischen Regeln untersuchen möchte. Sobald nötig werden dann Tokens und Parameter/Dateien von dem Benutzer bereitgestellt und ermöglicht somit eine dynamische Analyse. Falls keine dynamische Analyse durchgeführt werden soll, läuft die Analyse statisch und voll automatisiert ab. 


[1] https://www.oreilly.com/library/view/rest-api-design/9781449317904/