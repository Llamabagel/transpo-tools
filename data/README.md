#`NCR.osm.pbf`
The `NCR.osm.pbf` file is the OpenStreetMap data for the National Capital Region. It is fed into [OSRM](http://project-osrm.org/) in order to generate `shapes.txt` files from GTFS data.

## Updating the data
To update the NCR data, download the Québec and Ontario `.osm.pbf` files from Geofabrik (located [here](http://download.geofabrik.de/north-america/canada.html)). 

Merge the downloaded files using [osmium](https://osmcode.org/).

```shell script
osmium merge ontario-latest.osm.pbf quebec-latest.osm.pbf -o merged.osm.pbf
```

Next, extract the data for only the National Capital Region (to keep down on memory usage during processing)

```shell script
osmium extract -b -76.2616,45.1249,-75.1726,45.6620 merged.osm.pbf -o NCR.osm.pbf
```