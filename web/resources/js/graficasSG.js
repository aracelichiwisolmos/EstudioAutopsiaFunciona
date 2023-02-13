/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
var datosMB="C:\\Users\\Araceli\\Desktop\\MAESTRÍA\\EstudioAutopsiaFunciona\\corpus\\SD\\C\\SDMap\\centro_hospitalario\\matriz_binaria3.arff";
var x =d3.scale.linear()
        .domain([0, d3.max(datoMB)])
        .range([0, 500]);

function graficar(){
    d3.select(".barras")
            .selectAll("div")
            .data(datosMB)
            .enter().append('div')
            .style("width", function(d){
                return x(d)+"px"
    })
}