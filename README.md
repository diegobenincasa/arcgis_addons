**1º CGEO Eclipse workspace with ArcGIS Desktop 10.2+ Add-ins**
===================

This repository contains a fully configured Eclipse workspace with add-ins projects for ArcGIS Desktop 10.2+. Those projects are developed in Java and directed to feature acquisition using **Leica Photogrammetry Suite (LPS)** machines equipped with **ERDAS StereoAnalyst** extension.

-------------

Projects
-------------

For now, there are 5 (five) projects available, described above.

> **Note:**
> - Add-ins were compiled using JDK 1.8, but for compatibility with ArcObjects for Java, Eclipse was set to use 1.6 environment.
> - Add-ins are not supposed to work with 10.2- versions of ArcGIS Desktop.
> - Cloning this repository is not enough to compile the add-ins. It is mandatory to have JDK 1.6+ and ArcGIS 10.2+ with Java Developer Kit, and installing the plugin for Eclipse from within it.

-------------

### GabaritoAquisicao

This add-in provides an overlay drawing (circle or square) to function as an "aquisition pattern": features smaller than the size of the pattern are not supposed to be vectorized for the working scale.

Provides a toolbar with options to choose a working scale, a pattern and its format. Values for the last two options are obtained from a CSV file written by the user, in the following structure:
_**Pattern name**;**Pattern type**;**Pattern area**_

- ***Pattern name***: the name that should appear in the toolbar for the pattern
- ***Pattern type***: the type of the pattern (1 for linear, 2 for area)
	- 1 limits the pattern to be only a circle, and 2 allows for a circle or a square (user's choice)
- ***Pattern area***: based on the dimensions of the pattern, the area of it (in mm²)

First CSV line is never read, as the user might want to add the file structure in the first line (recommended).

-------------

### ExportaOrdemCamadas

When arranging layers in ArcMap layer tree, user tries to order them so no data is hidden. When finally reaches a desirable order, it would be nice if that order could be reused in future maps, reducing duplicate work.

Based on an opened MXD file, this add-in exports the order of the loaded layers (considering group layers) to a CSV file. It is useful for another add-in (**CarregaCamadas**).

It provides a toolbar with one button in it.

-------------

### ExportaLyr

During a map design, it is usual to define symbologies for the layers, like line style, labels, and so on. In many scenarios, different users should follow the same symbology standards, but errors in defining those symbols inside ArcMap are not so difficult to glimpse. It is desirable that all map designers follow the same coherent layer symbology.

This add-in exports all loaded layers to .lyr files, so their display representation could be reused in other maps. It is useful for another add-in (**CarregaCamadas**).

It provides a toolbar with one button in it.

-------------

### CarregaCamadas

Sometimes the MDB file to be opened has many empty datasets. Loading the entire database will probably slow down the application even when performing simple tasks like pan and zoom, as the software should calculate the bounding box for each loaded layer and query the database for the features to be drawn inside it. If database is huge, it takes unnecessary time to complete.

This add-in loads from the MDB only those datasets with more than 1 (one) feature in it, and creates layers to them respecting a previously defined layer order (here, **ExportaOrdemCamadas** is useful). In addition, it can also apply layer symbologies from saved .lyr files (here, **ExportaLyr** is useful).

Input layer order data is a CSV file formatted like the **ExportaOrdemCamadas** output. Input .lyr folder should have .lyr files with the same name as the layers in the workspace.

It provides a toolbar with 3 (three) buttons in it: select CSV with layers order, select MDB (after selecting, will automatically load datasets with features), and load layer styles (.lyr files).

-------------

### CotaCurvas

When digitizing elevation curves and elevation points, StereoAnalyst gives features a Z coordinate value. But the data structure used by the Brazilian Army Geographic Service Bureau (DSG) and the Brazilian National Cartography Committee (CONCAR) requires the "cota" (elevation) attribute to be filled, which StereoAnalyst is not aware of.

This add-in fills the "cota" attribute with the Z coordinate value for elevation curves and elevation points by user demand (clicking on a button will fill/update "cota" values for all those features) and/or automatically (after each feature creation using Editor). Automatic value fill is provided by add-in extension **Auto Cotar** (available, after add-in installation, inside Extension Manager).

Ir provides a toolbar with 2 (two) buttons in it: one for elevation curves, and other for elevation points. Also provides the extension **Auto Cotar**, which should be enabled to perform "cota" data auto-fill.

-------------

### Contact

If you have questions or suggestions, feel free to contact me.

> **1º Centro de Geoinformação (1º CGEO)**
> - Rua Cleveland, 250 - Santa Teresa
> - CEP 90.850-240
> - Porto Alegre, Rio Grande do Sul, Brazil
> - Phone: +55 (51) 3907-0648 (ext. 2137)</small
> - Mobile: +55(51) 99902-3892
> - E-mail: [benincasa.diego@eb.mil.br][1]

[1]: mailto:benincasa.diego@eb.mil.br
