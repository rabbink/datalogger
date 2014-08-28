package nl.rabbink.datalogger.view;

import com.sun.faces.renderkit.html_basic.RadioRenderer;
import java.io.IOException;
import javax.faces.component.UIComponent;
import javax.faces.component.UINamingContainer;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.FacesRenderer;

@FacesRenderer(componentFamily = "map", rendererType = CustomSelectOneRadioRenderer.RENDERER_TYPE)
public class CustomSelectOneRadioRenderer extends RadioRenderer {

    public static final String RENDERER_TYPE = "com.mycompany.pi.CustomSelectOneRadio";

//    @Override
//    public void encodeChildren(FacesContext context, UIComponent component) throws IOException {
//        ResponseWriter writer = context.getResponseWriter();
//        writer.startElement("div", component);
//        writer.writeText("hello world", null);
//        writer.endElement("div");
//    }
}
