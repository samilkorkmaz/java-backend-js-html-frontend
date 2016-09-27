package dragdropservlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import javax.json.spi.JsonProvider;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Demonstration of Java backend and HTML & JavaScript frontend. Example: drag-drop-snap of polygons where logic is
 * at the Java backend and view (drawing) is at the HTML & JavaScript frontend.
 *
 * @author samil korkmaz
 * @date September 2016
 * @license public domain
 */
@WebServlet("/DragDropServlet")
public class JavaBackend extends HttpServlet {

    private static final List<MyPolygon> polygonList = new ArrayList<>();
    private static final List<MyPolygon> snapPolygonList = new ArrayList<>();

    private boolean isSelected = false;
    int iSelected = -1;
    private int snapX = 400;
    private int snapY = 100;
    private static int prevMouseX;
    private static int prevMouseY;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("In Java");
        //Get data from JavaScript
        JsonReader jsonReader = JsonProvider.provider().createReader(request.getReader());
        JsonObject jsonFromJavaScript = jsonReader.readObject();
        //Prepare data to send to JavaScript
        boolean init = jsonFromJavaScript.getBoolean("init");
        if (init) {
            init();
        }
        boolean mouseDown = jsonFromJavaScript.getBoolean("mouseDown");
        int mouseX = jsonFromJavaScript.getInt("mouseX");
        int mouseY = jsonFromJavaScript.getInt("mouseY");

        System.out.println("mouseDown: " + mouseDown + ", x: " + mouseX + ", y: " + mouseY);
        if (!mouseDown) {
            isSelected = false;
        }

        if (!isSelected) {
            for (int iPoly = 0; iPoly < polygonList.size(); iPoly++) {
                if (polygonList.get(iPoly).contains(mouseX, mouseY)) {
                    isSelected = true;
                    iSelected = iPoly;
                    prevMouseX = mouseX;
                    prevMouseY = mouseY;
                    System.out.println("polygon contains xc yc! iSelected = " + iSelected);
                    break;
                }
            }
        }

        if (isSelected && mouseDown) {
            System.out.println("translate triangle");
            polygonList.get(iSelected).translate(mouseX - prevMouseX, mouseY - prevMouseY);
            prevMouseX = mouseX;
            prevMouseY = mouseY;
            polygonList.get(iSelected).isCloseTo(snapPolygonList.get(iSelected));
        }

        JsonObjectBuilder dataToJavaScript = Json.createObjectBuilder();
        JsonArrayBuilder dragPolys = Json.createArrayBuilder();
        for (MyPolygon polygon : polygonList) {
            JsonArrayBuilder coords = Json.createArrayBuilder();
            for (int i = 0; i < polygon.npoints; i++) {
                coords.add(Json.createObjectBuilder().add("x", polygon.xpoints[i]));
                coords.add(Json.createObjectBuilder().add("y", polygon.ypoints[i]));
            }
            dragPolys.add(Json.createObjectBuilder().add("coords", coords));
        }
        dataToJavaScript.add("dragPolys", dragPolys);

        JsonArrayBuilder snapPolys = Json.createArrayBuilder();
        for (MyPolygon snapPolygon : snapPolygonList) {
            JsonArrayBuilder coords = Json.createArrayBuilder();
            for (int i = 0; i < snapPolygon.npoints; i++) {
                coords.add(Json.createObjectBuilder().add("x", snapPolygon.xpoints[i]));
                coords.add(Json.createObjectBuilder().add("y", snapPolygon.ypoints[i]));
            }
            snapPolys.add(Json.createObjectBuilder().add("coords", coords));
        }
        dataToJavaScript.add("snapPolys", snapPolys);

        JsonObject jsonToJavaScript = dataToJavaScript.build();
        System.out.println("jsonToJavaScript: " + jsonToJavaScript);

        response.setContentType("text/plain");
        PrintWriter out = response.getWriter();
        out.println(jsonToJavaScript);

        //String dataToJavaScript = "{ \"x\": 700, \"y\": 200 }";
    }

    @Override
    public void init() throws ServletException {
        System.out.println("INIT");
        isSelected = false;
        iSelected = -1;
        int[] xPoints1 = {0, 100, 0};
        int[] yPoints1 = {0, 0, 100};

        int[] xPoints2 = {0, 100, 100, 0};
        int[] yPoints2 = {0, 0, 100, 100};

        int[] xPoints3 = {200, 300, 300, 50};
        int[] yPoints3 = {0, 0, 100, 100};

        MyPolygon poly1 = new MyPolygon(xPoints1, yPoints1);
        MyPolygon poly2 = new MyPolygon(xPoints2, yPoints2);
        MyPolygon poly3 = new MyPolygon(xPoints3, yPoints3);
        MyPolygon snapPoly1 = new MyPolygon(xPoints1, yPoints1);
        MyPolygon snapPoly2 = new MyPolygon(xPoints2, yPoints2);
        MyPolygon snapPoly3 = new MyPolygon(xPoints3, yPoints3);
        prevMouseX = 0;
        prevMouseY = 0;

        snapPoly1.translate(snapX, snapY);
        snapPoly2.translate(snapX + 100, snapY);
        snapPoly3.translate(snapX - 300, snapY);

        polygonList.clear();
        polygonList.add(poly1);
        polygonList.add(poly2);
        polygonList.add(poly3);

        snapPolygonList.clear();
        snapPolygonList.add(snapPoly1);
        snapPolygonList.add(snapPoly2);
        snapPolygonList.add(snapPoly3);
    }

    @Override
    public void destroy() {
        super.destroy();
    }

}
