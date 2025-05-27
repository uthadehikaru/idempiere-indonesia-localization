package id.rekaestudigital.idempiere.editorjs.window;

import org.adempiere.webui.LayoutUtils;
import org.adempiere.webui.component.ConfirmPanel;
import org.adempiere.webui.component.Window;
import org.adempiere.webui.util.ZKUpdateUtil;
import org.compiere.util.CLogger;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Div;
import org.zkoss.zul.Html;
import org.zkoss.zul.Separator;
import org.zkoss.zul.Vlayout;

import org.zkoss.json.JSONObject;

public class WEditorJsDialog extends Window implements EventListener<Event>{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4897777166061972170L;
	private static final CLogger log = CLogger.getCLogger(WEditorJsDialog.class);
	private static final String ON_EDITOR_SAVE_EVENT = "onEditorSave"; 

	private String title;
	private String text;
	private String id;
	private boolean cancelled;

	private boolean editable;

	public WEditorJsDialog(String title, String text, boolean isReadWrite, int length) {
		super();
		this.title = title;
		this.text = text;
		this.editable = isReadWrite;
		id = getUuid();
		init();
	}

	private void init() {
		setTitle(title);
		setBorder("normal");
		ZKUpdateUtil.setWindowHeightX(this, 450);
		ZKUpdateUtil.setWindowWidthX(this, 800);
		setStyle("position: absolute;");
		setClosable(true);
		setSizable(true);
		setMaximizable(true);
		setSclass("popup-dialog editorjs-dialog");
		
		Vlayout vbox = new Vlayout();
		appendChild(vbox);
		ZKUpdateUtil.setWidth(vbox, "100%");
		ZKUpdateUtil.setVflex(vbox, "true");
		vbox.setSclass("dialog-content");
		
		Div div = new Div();
		ZKUpdateUtil.setHeight(div, "100%");
		ZKUpdateUtil.setWidth(div, "100%");
		LayoutUtils.addSclass("html-field", div);
		div.setStyle("overflow: auto; border: 1px solid");
		vbox.appendChild(div);
		Html html = new Html();
		div.appendChild(html);
		html.setContent("<div id=\"editor"+id+"\"></div>"
		        + "<script src=\"https://cdn.jsdelivr.net/npm/@editorjs/editorjs@latest\"></script>"
		        + "<script>"
		        + "  setTimeout(function(){window.editor"+id+" = new EditorJS({"
		        + "    holder: 'editor"+id+"',"
		        + "	   data: " + text + ","
		        + "  })},100);"
		        + "</script>");
		
		vbox.appendChild(new Separator());
		
		Div footer = new Div();
		footer.setSclass("dialog-footer");
		ConfirmPanel confirmPanel = new ConfirmPanel(true);
		footer.appendChild(confirmPanel);
		confirmPanel.addActionListener(this);
		appendChild(footer);
		
		addEventListener(ON_EDITOR_SAVE_EVENT, this);
		log.info("Event listener for onEditorSave registered.");
	}

	@Override
	public void onEvent(Event event) throws Exception {
		log.info(event.getName());
		if (event.getName().equals(ON_EDITOR_SAVE_EVENT)) { 
			JSONObject data =(JSONObject) event.getData();
			processEditorData(data);
		} else if (event.getTarget().getId().equals(ConfirmPanel.A_CANCEL)) {
			onCancel();
		} else if (event.getTarget().getId().equals(ConfirmPanel.A_OK)) {
			if (editable) {
				onSave();
			}			
		}
	}
	
	private void onSave() {
		StringBuffer jsCode = new StringBuffer();
		jsCode.append("window.editor").append(id).append(".save().then((outputData) => { ")
		.append("setTimeout(function(){zAu.send(new zk.Event(zk.Widget.$('#"
		+ getUuid() + "'), '" + ON_EDITOR_SAVE_EVENT + "', outputData));}, 100);")
		.append("}).catch((error) => { ")
		.append("console.log('Saving failed: ', error); ")
		.append("});");

		Clients.evalJavaScript(jsCode.toString());
    }

	private void onCancel() {
		cancelled = true;
		detach();
	}
	
	private void processEditorData(JSONObject data) {
		try {
	        text = data.toString();
	        detach();
	    } catch (Exception e) {
	        log.warning("Error processing EditorJS data: " + e.getMessage());
	    }
	}

	public boolean isCancelled() {
		return cancelled;
	}

	public String getText() {
		return text;
	}
}
