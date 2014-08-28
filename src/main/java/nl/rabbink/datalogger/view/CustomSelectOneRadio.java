package nl.rabbink.datalogger.view;

import com.sun.faces.renderkit.RenderKitUtils;
import com.sun.faces.util.RequestStateManager;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import javax.el.ELException;
import javax.faces.component.FacesComponent;
import javax.faces.component.UIComponent;
import javax.faces.component.UINamingContainer;
import javax.faces.component.UISelectOne;
import javax.faces.component.html.HtmlSelectOneRadio;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.model.SelectItem;

@FacesComponent("customSelectOneRadio")
public class CustomSelectOneRadio extends HtmlSelectOneRadio {

//    public CustomSelectOneRadio() {
//        // Set default renderer.
//        setRendererType(CustomSelectOneRadioRenderer.RENDERER_TYPE);
//    }
//    @Override
//    public void encodeChildren(FacesContext context) throws IOException {
//        ResponseWriter writer = context.getResponseWriter();
//        writer.startElement("div", this);
//        writer.writeText("hello world", null);
//        writer.endElement("div");
//    }
    @Override
    public void encodeEnd(FacesContext context) throws IOException {
        renderBeginText(this, context);

    }

//    @Override
//    public String getFamily() {
//        return "map";
//    }
    private void renderBeginText(UIComponent component, FacesContext context) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        assert (writer != null);

        Iterator<SelectItem> items
                = RenderKitUtils.getSelectItems(context, component);

        UISelectOne selectOne = (UISelectOne) component;
        Object curValue = selectOne.getSubmittedValue();
        if (curValue == null) {
            curValue = selectOne.getValue();
        }
        Class type = String.class;
        if (curValue != null) {
            type = curValue.getClass();
            if (type.isArray()) {
                curValue = ((Object[]) curValue)[0];
                if (null != curValue) {
                    type = curValue.getClass();
                }
            } else if (Collection.class.isAssignableFrom(type)) {
                Iterator valueIter = ((Collection) curValue).iterator();
                if (null != valueIter && valueIter.hasNext()) {
                    curValue = valueIter.next();
                    if (null != curValue) {
                        type = curValue.getClass();
                    }
                }
            }
        }

        int i = -1;
        while (items.hasNext()) {
            SelectItem selectItem = items.next();
            i++;

            Object itemValue = selectItem.getValue();
            RequestStateManager.set(context,
                    RequestStateManager.TARGET_COMPONENT_ATTRIBUTE_NAME,
                    component);
            Object newValue;
            try {
                newValue = context.getApplication().getExpressionFactory().
                        coerceToType(itemValue, type);
            } catch (ELException | IllegalArgumentException ele) {
                newValue = itemValue;
            }

            boolean checked = null != newValue && newValue.equals(curValue);

            writer.startElement("div", component);

            String styleClass = (String) component.getAttributes().get(
                    "styleClass");
            String style = (String) component.getAttributes().get("style");
            if (styleClass != null) {
                writer.writeAttribute("class", styleClass, "class");
            }
            if (style != null) {
                writer.writeAttribute("style", style, "style");
            }

            writer.writeText("\n", component, null);

            writer.startElement("label", component);

            writer.writeText("\n", component, null);

            writer.startElement("input", component);
            writer.writeAttribute("name", component.getClientId(context),
                    "clientId");

            writer.writeAttribute("value", "figure_out_" + i, "value");
            writer.writeAttribute("type", "radio", null);

            if (checked) {
                writer.writeAttribute("checked", Boolean.TRUE, null);
            }

            writer.endElement("input");

            writer.writeText("\n", component, null);
            writer.writeText(selectItem.getLabel(), component, "label");
            writer.endElement("label");

            writer.writeText("\n", component, null);

            writer.endElement("div");
        }
    }

}
