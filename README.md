# ItalianDudes Launcher
Un launcher in JavaFX per tutte le applicazioni scritte e sviluppate da ItalianDudes.
L'applicazione richiede Java 21 e un architettura a 64bit. In fondo è disponibile la [guida all'installazione di Java 21](#installare-java-21).

# Installare Java 21
- In base al sistema operativo in cui ti trovi, la procedura di installazione cambia.
- Lista sistemi operativi:
    - [Installare Java 21 su Windows](#installare-java-21-su-windows).
    - [Installare Java 21 su Linux con "apt"](#installare-java-21-su-linux-con-apt).
    - [Installare Java 21 su Linux con "pacman"](#installare-java-21-su-linux-con-pacman).
- In caso stessi usando MacOS, puoi scaricare il DMG della JDK da [qui](https://www.oracle.com/it/java/technologies/downloads/#java21).

## Installare Java 21 su Windows
- Per scaricare java 21 su Windows servirà recarsi al [link di download di Java 21](https://www.oracle.com/it/java/technologies/downloads/#java21).
- Accedendo alla pagina, basterà scendere in basso e si troverà la tabella "Windows" con i relativi installer.
- Scaricato l'installer, eseguirlo come un qualunque programma di Windows e completare l'installazione.
- Se non ci sono errori, avrai installato correttamente Java 21 sul sistema operativo.

## Installare Java 21 su Linux con "apt"
- Se devi installare Java 21 su un sistema operativo con "apt" come package manager, probabilmente non ti serve questa guida.
- Apri il terminale.
- Prima di installare Java verifica se hai già Java 21 installato.
- Per fare ciò digita su terminale `java -version`.
- Se "java" non è stato trovato, non è presente una versione di java indicizzata sul tuo sistema operativo.
- Se invece ottieni una risposta, verifica che mostri una versione di Java 21, e non un'altra versione.
- Se non è stato trovato, oppure la versione installata non è quella corretta, continua a seguire questa guida.
- Prima di installare Java, eseguiamo prima un comando di routine per aggiornare le repository: `sudo apt-get update`.
- Ora cerchiamo quali versioni di Java 21 possiamo scaricare attraverso il comando `sudo apt list openjdk-21*`.
- Probabilmente troverai tra le varie versioni disponibili una chiamata `openjdk-21-jre`, ci interessa quella.
- Per scaricarla ora esegui il comando `sudo apt-get install openjdk-21-jre`.
- Se non ci sono errori, avrai installato correttamente Java 21 sul sistema operativo.

## Installare Java 21 su Linux con "pacman"
- DISCLAIMER: non ho mai usato un sistema Linux con "pacman" come package manager, le seguenti istruzioni sono state ricavate dal web e poi controllate al fine di usare solo link di download ufficiali e comandi non ambigui.
- Se devi installare Java 21 su un sistema operativo con "pacman" come package manager, probabilmente non ti serve questa guida.
- Apri il terminale.
- Prima di installare Java verifica se hai già Java 21 installato.
- Per fare ciò digita su terminale `java -version`.
- Se "java" non è stato trovato, non è presente una versione di java indicizzata sul tuo sistema operativo.
- Se invece ottieni una risposta, verifica che mostri una versione di Java 21, e non un'altra versione.
- Se non è stato trovato, oppure la versione installata non è quella corretta, continua a seguire questa guida.
- Prima di installare Java, controlliamo che versioni di Java sono disponibili attraverso il comando `sudo pacman -sS java | grep jre21`.
- In teoria, dovresti trovare una versione chiamata `jre21-openjdk`, scaricheremo quella.
- Per scaricare quella versione, esegui `sudo pacman -S jre21-openjdk`.
- Se non ci sono errori, avrai installato correttamente Java 21 sul sistema operativo.