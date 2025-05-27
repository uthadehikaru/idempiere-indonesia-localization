package id.rekaestudigital.idempiere.editorjs.component;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

import org.adempiere.base.IDisplayTypeFactory;
import org.compiere.model.MReference;
import org.compiere.util.DB;
import org.compiere.util.Language;

import id.rekaestudigital.idempiere.editorjs.editor.WJsEditor;

public class DisplayTypeFactory implements IDisplayTypeFactory{

	@Override
	public boolean isID(int displayType) {
		return false;
	}

	@Override
	public boolean isNumeric(int displayType) {
		return false;
	}

	@Override
	public Integer getDefaultPrecision(int displayType) {
		return null;
	}

	@Override
	public boolean isText(int displayType) {
		return true;
	}

	@Override
	public boolean isDate(int displayType) {
		return false;
	}

	@Override
	public boolean isLookup(int displayType) {
		return false;
	}

	@Override
	public boolean isLOB(int displayType) {
		return false;
	}

	@Override
	public DecimalFormat getNumberFormat(int displayType, Language language, String pattern) {
		return null;
	}

	@Override
	public SimpleDateFormat getDateFormat(int displayType, Language language, String pattern) {
		return null;
	}

	@Override
	public Class<?> getClass(int displayType, boolean yesNoAsBoolean) {
		MReference ref = MReference.get(displayType);
        if(ref != null && WJsEditor.REFERENCE_EDITOR_JS.equals(ref.getName())) {
        	return String.class;
        }
        return null;
	}

	@Override
	public String getSQLDataType(int displayType, String columnName, int fieldLength) {
        MReference ref = MReference.get(displayType);
        if(ref != null && WJsEditor.REFERENCE_EDITOR_JS.equals(ref.getName())) {
        	return DB.getDatabase().getJsonDataType();
        }
        return null;
	}

	@Override
	public String getDescription(int displayType) {
		MReference ref = MReference.get(displayType);
        if(ref != null && WJsEditor.REFERENCE_EDITOR_JS.equals(ref.getName())) { 
        	return ref.getDescription();
        }
        return null;
	}

}
