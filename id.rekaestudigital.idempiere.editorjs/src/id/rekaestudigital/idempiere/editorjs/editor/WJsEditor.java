package id.rekaestudigital.idempiere.editorjs.editor;

import java.util.logging.Level;

import org.adempiere.webui.LayoutUtils;
import org.adempiere.webui.adwindow.ADWindow;
import org.adempiere.webui.adwindow.AbstractADWindowContent;
import org.adempiere.webui.editor.IEditorConfiguration;
import org.adempiere.webui.editor.WEditor;
import org.adempiere.webui.event.DialogEvents;
import org.adempiere.webui.event.ValueChangeEvent;
import org.adempiere.webui.session.SessionManager;
import org.apache.ecs.xhtml.p;
import org.compiere.model.GridField;
import org.compiere.util.CLogger;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Div;
import org.zkoss.zul.Html;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import id.rekaestudigital.idempiere.editorjs.window.WEditorJsDialog;

public class WJsEditor extends WEditor {

	private Html  box = null;

	private boolean readwrite = true;
	private String oldValue;

	private AbstractADWindowContent adwindowContent;
	
	private static final CLogger log = CLogger.getCLogger(WJsEditor.class);

	private static final String BLOCK_PARAGRAPH = "paragraph";

	public static final Object REFERENCE_EDITOR_JS = "Editor JS";
	
	public WJsEditor(GridField gridField, boolean tableEditor, IEditorConfiguration editorConfiguration)
    {
        super(new Div(), gridField, tableEditor, editorConfiguration);
        init();
    }
	
	private void init() {
		if (log.isLoggable(Level.INFO)) log.info("Initializing editor js component");

    	if (gridField != null)
    	{
			Div div = (Div) getComponent();
			div.setHeight("96px");
			div.setWidth("100%");
			div.setStyle("padding:5px;background:white;");
			LayoutUtils.addSclass("html-field", div);
			div.addEventListener(Events.ON_CLICK, this);

			box = new Html();
			box.setParent(div);
    	}
	}
	
	@Override
    public Div getComponent() {
    	return (Div) component;
    }

	@Override
	public void onEvent(Event event) throws Exception {
		if (Events.ON_CLICK.equals(event.getName()) && readwrite) {
			openDialog();
		}
	}

	private void openDialog() {
		adwindowContent = findADWindowContent();
		final WEditorJsDialog dialog = new WEditorJsDialog(gridField.getVO().Header, getDisplay(),
				isReadWrite(), gridField.getFieldLength());
		dialog.addEventListener(DialogEvents.ON_WINDOW_CLOSE, new EventListener<Event>() {

			@Override
			public void onEvent(Event event) throws Exception {
				if (adwindowContent != null) {
					adwindowContent.hideBusyMask();
				}
				if (!dialog.isCancelled()) {
					String newText = dialog.getText();
					ValueChangeEvent changeEvent = new ValueChangeEvent(WJsEditor.this, WJsEditor.this.getColumnName(), oldValue, newText);
					WJsEditor.super.fireValueChange(changeEvent);
					oldValue = newText;
					box.setContent(convertHtml());
				}
			}
		});
		if (adwindowContent != null) 
		{
			adwindowContent.getComponent().getParent().appendChild(dialog);
			adwindowContent.showBusyMask(dialog);
			LayoutUtils.openOverlappedWindow(adwindowContent.getComponent().getParent(), dialog, "middle_center");
		}
		else
		{
			SessionManager.getAppDesktop().showWindow(dialog);
		}			
		dialog.focus();
	}
	
	protected String convertHtml() {
		JsonObject json = new Gson().fromJson(oldValue, JsonObject.class);
		StringBuffer html = new StringBuffer();
		if(json !=null && json.has("blocks")) {
			for(JsonElement item : json.get("blocks").getAsJsonArray()) {
				JsonObject block = (JsonObject) item;
				String type = block.get("type").getAsString();
				JsonObject data = block.get("data").getAsJsonObject();
				if(BLOCK_PARAGRAPH.equals(type)) {
					p paragraph = new p();
					paragraph.addElement(data.get("text").getAsString());
					html.append(paragraph);
				}
				
			}
		}
		return html.toString();
	}

	private AbstractADWindowContent findADWindowContent() {
		Component parent = getComponent().getParent();
		while(parent != null) {
			if (parent.getAttribute(ADWindow.AD_WINDOW_ATTRIBUTE_KEY) != null) {
				ADWindow adwindow = (ADWindow) parent.getAttribute(ADWindow.AD_WINDOW_ATTRIBUTE_KEY);
				return adwindow.getADWindowContent();
			}
			parent = parent.getParent();
		}
		return null;
	}

	@Override
	public void setReadWrite(boolean readWrite) {
		this.readwrite = readWrite;
	}

	@Override
	public boolean isReadWrite() {
		return readwrite;
	}

    @Override
	public String getDisplayTextForGridView(Object value) {
		if (value == null) {
			return "";
		} else {
			return (String)value;
		}
    }
	
	
	@Override
	public Component getDisplayComponent() {
		return new Html();
	}

	@Override
	public void setValue(Object value) {
		if (value != null)
        {
			oldValue = value.toString();
        	box.setContent(convertHtml());
        }
        else
        {
        	box.setContent("");
        }
	}

	@Override
	public Object getValue() {
		return oldValue;
	}

	@Override
	public String getDisplay() {
		if(oldValue==null)
			return "{}";
		
		return oldValue;
	}

}
