var mouseDown = false;
var init = true;
var NO_FILL = "no fill";

function drawPolygon(context, coords, fillStyle) {
    context.beginPath();
    context.moveTo(coords[0].x, coords[1].y);
    for (var i = 2; i < coords.length - 1; i += 2) {
        context.lineTo(coords[i].x, coords[i + 1].y);
    }
    context.lineTo(coords[0].x, coords[1].y);
    //alert("x: " + polygonCoords[0].x + ", y: " + polygonCoords[1].y)
    context.stroke();
    context.closePath();
    if (fillStyle.localeCompare(NO_FILL) !== 0) {
        context.fillStyle = fillStyle;
        context.fill();
    }
}

function updateBoard(jsonFromJava) {
    context = clearCanvas();
    var data = JSON.parse(jsonFromJava);
    //draw dragged polygons:                
    for (var iPoly = 0; iPoly < data.dragPolys.length; iPoly++) {
        drawPolygon(context, data.dragPolys[iPoly].coords, 'rgba(255, 0, 0, 0.5)');
    }
    //draw snap polygons:
    for (var iPoly = 0; iPoly < data.snapPolys.length; iPoly++) {
        drawPolygon(context, data.snapPolys[iPoly].coords, NO_FILL);
    }
}

function clearCanvas() {
    var canvas = document.getElementById("board");
    context = canvas.getContext("2d");
    context.clearRect(0, 0, canvas.width, canvas.height);
    return context;
}

function onMouseDown(e) {
    mouseDown = true;
}

function onMouseUp(e) {
    mouseDown = false;
}

function onMouseMove(e) {
    var bounds = e.target.getBoundingClientRect();
    var jsonToJava = {
        init: false,
        mouseDown: mouseDown,
        mouseX: e.clientX - bounds.left,
        mouseY: e.clientY - bounds.top
    };
    communicateWithJava(jsonToJava);
}

function communicateWithJava(jsonToJava) {
    var xhr = new XMLHttpRequest();
    xhr.onreadystatechange = function () {
        if (xhr.readyState === 4) {
            var jsonFromJava = xhr.responseText;
            updateBoard(jsonFromJava);
        }
    };
    xhr.open('POST', 'NewServlet', true);
    xhr.send(JSON.stringify(jsonToJava));
}

function initCanvas() {
    var jsonToJava = {
        init: true,
        mouseDown: false,
        mouseX: -1,
        mouseY: -1
    };
    communicateWithJava(jsonToJava);
}

