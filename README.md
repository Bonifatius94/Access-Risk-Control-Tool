# Access Risk Control Tool (ART)

## About
This project provides an offline, multi-platform, rich-client administration tool for analyzing SAP ERP user permission rules, written in JavaFX. As a first step it lets you define locally stored authorization rules and patterns to be searched for (using an encrypted H2 file database for storage). In the next step, those rules are transformed into an SAP ABAP query that is processed by a SAP server to perform the data retrieval. By evaluating those query results, the admin can check for exceptions in the authorization patterns deployed to the SAP server using in-app plotting tools. For a convenient offline user experience, all query results are persisted in the local H2 database as well to have a look at them once again. Finally, the user can export his findings as Word / CSV / PDF to share it with colleagues.

*Info: This project won the internal software programming competition of 2018 at the University of Augsburg against ~20 other teams of 5-6 members each. *

## Project Structure
- *art/* contains the source code including instructions how to build / deploy
- *documentation* contains software design documents (written in German)
- *aufgaben* / *analyze* contain test projects created to explore the Java tech stack
- *review* contains the final release version published to the software programming competition

## Authors
- Merlin Albes
- Marco Tröster
- Franz Schulze
- Stefan Jung
- Joshua Schreibeis
