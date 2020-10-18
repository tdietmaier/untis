# Quickstart

DB-Zugang etc. in application.yaml konfigurieren

Beispielaufruf (cygwin)

$ curl -i -s -X POST localhost:8080/messages -d '{"id":1002,"text":"test 123 öäüßµ€Êúù"}' -H "content-type: application/json;charset=ISO-8859-1"

# Designentscheidungen

Dass die ID als Kafka-key zu verwenden ist, hab ich einfach angenommen; Detto, dass POSTs mit gleicher ID die message ohne Fehlermeldung überschreiben sollen.
Eine bestimmte Reihenfolge der Übertragung nach Kafka wird nicht garantiert.

Entities werden der Einfachkeit halber gleich als DTOs verwendet, das geht m. E. nur bei so kleinen und einfachen Anwendungen gut.

## Eigene Queue-Tabelle

Alternativ zur eigenen Queue-Tabelle kann auch ein Sendestatus in der Message-Tabelle geführt werden.
* Wir würden uns ein INSERT sparen, müssen aber den index auf den Sendestatus (den es sinnvollerweise geben wird) aktualiseren, was den Performancevorteil zumindest verringert
* In einem echten System können beim update des Sendestatus u. U. optimistic locking-Fehler entstehen, die fachlich nicht gerechtfertigt sind.
* In einem echten System wird die message u. U. verändert, bevor wir sie an Kafka gesendet haben.

## Nicht immer in die Queue-Tabelle schreiben

Das System könnte nur in die Queue-Tabelle schreiben, wenn das Senden an Kafka fehlschlägt
* Der Client müsste ein Timeout abwarten, wenn Kafka nicht erreichbar ist; unnötiger Performanceverlust, weil wir dem REST-Client ohnehin nicht zusagen, dass die message an Kafka übertragen wurde.

## Transaction Manager verwenden

Wir könnten Kafka als NonXA-Ressource in einen transaction manager einbinden und gemeinsam mit Postgres committen. Ich hab dafür nichts Fertiges ergoogelt und
nachdem keine consistency erforderlich ist habe ich es nicht weiter verfolgt.
