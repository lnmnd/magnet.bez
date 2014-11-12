# Magnet loturak: bezeroa

## Garapena

### REPL bidezkoa

cljs REPL abiarazteko (phantomjs instalatuta eduki behar da):

> => (cemerick.piggieback/cljs-repl :repl-env (cemerick.austin/exec-env))

Browser REPL:

> => (cemerick.austin.repls/exec :exec-cmds ["firefox"])

Ateratzeko:

> => :cljs/quit

### Interaktiboa

> lein figwheel

Nabigatzailearekin http://localhost:8080 helbidera joan.

Kodea aldatzean orria automatikoki berrituko da.