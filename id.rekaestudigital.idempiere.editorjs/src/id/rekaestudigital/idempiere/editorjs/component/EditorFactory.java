package id.rekaestudigital.idempiere.editorjs.component;

import org.adempiere.webui.editor.IEditorConfiguration;
import org.adempiere.webui.editor.WEditor;
import org.adempiere.webui.factory.IEditorFactory;
import org.compiere.model.GridField;
import org.compiere.model.GridTab;
import org.compiere.model.MReference;
import org.compiere.util.CLogger;

import id.rekaestudigital.idempiere.editorjs.editor.WJsEditor;

public class EditorFactory implements IEditorFactory {
	
	CLogger log = CLogger.getCLogger(EditorFactory.class);

	@Override
	public WEditor getEditor(GridTab gridTab, GridField gridField, boolean tableEditor) {
		return getEditor(gridTab, gridField, tableEditor, null);
	}

	@Override
	public WEditor getEditor(GridTab gridTab, GridField gridField, boolean tableEditor,
			IEditorConfiguration editorConfiguration) {
		if (gridField == null)
        {
            return null;
        }

        WEditor editor = null;
        int displayType = gridField.getDisplayType();
        MReference ref = MReference.get(displayType);
        if(id.rekaestudigital.idempiere.editorjs.editor.WJsEditor.REFERENCE_EDITOR_JS.equals(ref.getName())){
	        editor = new WJsEditor(gridField, tableEditor, editorConfiguration);
        }
        if(editor != null)
	        editor.setTableEditor(tableEditor);

        return editor;
	}

}
