# Magnet loturak: bezeroa

## Konfigurazioa

konfig.js kopiatu ondorengo kokapenetara eta parametroak aldatu
behar izanez gero:

- Garapena: resources/public/konfig.js
- Banaketa: dist/konfig.js

Garapenean figwheel true izatea gomendatzen da eta banaketan false.

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

## Banatzeko bertsioa prestatu

> lein cljsbuild once prod

dist karpeta prest geratuko da.