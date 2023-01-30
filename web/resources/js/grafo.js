/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

function pintaGrafo( nodos,  arcos){
     // create an array with nodes
                            var nodes = new vis.DataSet(nodos);
                            // create an array with edges
                            var edges = new vis.DataSet(arcos);
                            // create a network
                            var container = document.getElementById('mynetwork');
                            var data = {
                                nodes: nodes,
                                edges: edges
                            };
                            var options = {
                                layout: {
                                    hierarchical: {
                                        direction: "UD"
                                    }

                                },
                                interaction: {
                                    navigationButtons: true,
                                    keyboard: true
                                }

                            };
                            var network = new vis.Network(container, data, options);
                            
                            // add event listeners
                            network.on('select', function (params) {
                                document.getElementById('apAlgoritmos:frmFiltrar:clase').value = params.nodes;
                                document.getElementById('apAlgoritmos:frmFiltrar:btTCP').onclick();
                            });
                          
}

function pintaGrafoResultado( nodos,  arcos){
     // create an array with nodes
                            var nodes = new vis.DataSet(nodos);
                            // create an array with edges
                            var edges = new vis.DataSet(arcos);
                            // create a network
                            var container = document.getElementById('mynetwork');
                            var data = {
                                nodes: nodes,
                                edges: edges
                            };
                            var options = {
                                layout: {
                                    hierarchical: {
                                        direction: "UD"
                                    }

                                },
                                interaction: {
                                    navigationButtons: true,
                                    keyboard: true
                                }

                            };
                            var network = new vis.Network(container, data, options);
                            
                            // add event listeners
                            network.on('select', function (params) {
                                document.getElementById('apAlgoritmos:frmFiltrar:clase').value = params.nodes;
                                document.getElementById('apAlgoritmos:frmFiltrar:btTCP').onclick();
                            });
                          
}

