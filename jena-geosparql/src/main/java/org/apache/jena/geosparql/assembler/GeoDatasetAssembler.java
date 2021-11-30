/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.jena.geosparql.assembler;

import io.github.galbiston.rdf_tables.datatypes.DatatypeController;

import static org.apache.jena.geosparql.assembler.GeoVocab.pDataset ;
import static org.apache.jena.geosparql.assembler.GeoVocab.geoDataset ;

import java.lang.invoke.MethodHandles;

import org.apache.jena.assembler.Assembler ;
import org.apache.jena.assembler.Mode ;
import org.apache.jena.assembler.assemblers.AssemblerBase ;
import org.apache.jena.geosparql.configuration.GeoSPARQLConfig;
import org.apache.jena.geosparql.implementation.datatype.GMLDatatype;
import org.apache.jena.geosparql.implementation.datatype.GeometryDatatype;
import org.apache.jena.geosparql.implementation.datatype.WKTDatatype;
import org.apache.jena.query.Dataset ;
import org.apache.jena.rdf.model.Resource ;
import org.apache.jena.sparql.util.graph.GraphUtils ;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class GeoDatasetAssembler extends AssemblerBase implements Assembler
{
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public static Resource getType() { return geoDataset ; }

    /*
<#geo_dataset> rdf:type     jgeo:Dataset ;
    jgeo:dataset <#dataset> ;
    jgeo:index   <#index> ;
    .

     */

    @Override
    public Dataset open(Assembler a, Resource root, Mode mode)
    {
        LOGGER.debug("Registering types");
        registerDatatypes();

        //Convert Geo predicates to Geometry Literals.
        // if (argsConfig.isConvertGeoPredicates()) //Apply validation of Geometry Literal.
        // {
        //     GeoSPARQLOperations.convertGeoPredicates(dataset, argsConfig.isRemoveGeoPredicates());
        // }

        //Apply hasDefaultGeometry relations to single Feature hasGeometry Geometry.
        // if (argsConfig.isApplyDefaultGeometry()) {
        //     GeoSPARQLOperations.applyDefaultGeometry(dataset);
        // }

        //Apply GeoSPARQL schema and RDFS inferencing to the dataset.
        // if (argsConfig.isInference()) {
        //     GeoSPARQLOperations.applyInferencing(dataset);
        // }

        Resource dataset = GraphUtils.getResourceValue(root, pDataset) ;

        // TODO - on-disk index which is only re-generated on startup
        // Resource index   = GraphUtils.getResourceValue(root, pIndex) ;

        LOGGER.debug("Loading dataset: %s", dataset.toString());
        Dataset ds = (Dataset)a.open(dataset);

        // TODO - memory index configuration is global, not tied to dataset
        // (whereas on-disk indexing is tied to a specific dataset)
        LOGGER.debug("Setting up memory index");
        // TODO - configurable size
        GeoSPARQLConfig.setupMemoryIndex();

        LOGGER.debug("Setup complete");

        return ds ;
    }

    public static final void registerDatatypes() {
        DatatypeController.addPrefixDatatype("wkt", WKTDatatype.INSTANCE);
        DatatypeController.addPrefixDatatype("gml", GMLDatatype.INSTANCE);
        GeometryDatatype.registerDatatypes();
    }

        /*
    private static void prepareSpatialExtension(Dataset dataset, ArgsConfig argsConfig) throws SpatialIndexException {

        // Transaction now required to check if dataset is empty.
        dataset.begin(ReadWrite.READ);
        boolean isEmpty = dataset.isEmpty();
        dataset.end();

        // Only build spatial index if data provided.
        if (!isEmpty) {
            if (argsConfig.getSpatialIndexFile() != null) {
                File spatialIndexFile = argsConfig.getSpatialIndexFile();
                GeoSPARQLConfig.setupSpatialIndex(dataset, spatialIndexFile);
            } else if (argsConfig.isTDBFileSetup()) {
                File spatialIndexFile = new File(argsConfig.getTdbFile(), SPATIAL_INDEX_FILE);
                GeoSPARQLConfig.setupSpatialIndex(dataset, spatialIndexFile);
            } else {
                GeoSPARQLConfig.setupSpatialIndex(dataset);
            }
        } else {
            LOGGER.warn("Datset empty. Spatial Index not constructed. Server will require restarting after adding data and any updates to build Spatial Index.");
        }
    }
    */
}
