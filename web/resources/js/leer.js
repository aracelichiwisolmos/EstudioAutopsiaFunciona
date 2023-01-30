/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

//document.getElementById("cargaDatos").addEventListener("click", cargarDatos);

/* <![CDATA[ */
//function cargarDatos() {

var xhr = new XMLHttpRequest();

xhr.onreadystatechange = function () {
    if (this.readyState == 4 && this.status == 200) {
        cargarXML(this);
    }

};
xhr.open("GET", "algorithms.xml", true);
xhr.send();
//}

function  cargarXML(xml) {
    var docXML = xml.responseXML;
    var tabla = "<tr><th>Algoritmo</th><th>default</th></tr>";
    var aDGCP = docXML.getElementsByTagName("algorithm");
    for (var i = 0; i < aDGCP.length; i++) {
        tabla += "<tr><td>";
        tabla += aDGCP[i].getElementsByTagName("name")[0].textContent;
        tabla += "</td><td>";
        tabla += aDGCP[i].getElementsByTagName("default")[0].textContent;
        tabla += "</td></tr>";
    }
    document.getElementById("sopF").innerHTML = tabla;
}

/* ]]> */