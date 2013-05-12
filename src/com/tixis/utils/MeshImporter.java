package com.tixis.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.zip.GZIPInputStream;

import rajawali.BaseObject3D;
import rajawali.materials.TextureManager;
import rajawali.parser.AParser.ParsingException;
import rajawali.parser.ObjParser;
import rajawali.utils.ModelUtils;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.util.Log;

import com.mydomain.wallpaper.mywallpaper.ExternalizedObject3D;
import com.mydomain.wallpaper.mywallpaper.R;

public class MeshImporter {
	public static BaseObject3D getObject(Resources resources,
			TextureManager mTextureManager, int objId) throws ParsingException {
		BaseObject3D obj;
		ObjParser parser = new ObjParser(resources, mTextureManager, objId);
		parser.parse();
		obj = parser.getParsedObject();
		return obj;
	}

	public static BaseObject3D getObject(ExternalizedObject3D ext) {
		BaseObject3D obj = ModelUtils.getObject3d(ext);
		int c = ext.getChildrenCount();
		for (int i = 0; i < c; i++) {
			ExternalizedObject3D extChild = ext.getChild(i);
			obj.addChild(getObject(extChild));
		}
		return obj;
	}

	public static BaseObject3D getObject(InputStream is) throws IOException,
			ClassNotFoundException {
		ObjectInputStream ois = null;
		BaseObject3D obj = null;
		try {
			GZIPInputStream gz = new GZIPInputStream(is);
			ois = new ObjectInputStream(gz);
			Log.d("Test", "Loading serialized object");
			ExternalizedObject3D externalized = (ExternalizedObject3D) ois
					.readObject();
			obj = ModelUtils.getObject3d(externalized);
			int c = externalized.getChildrenCount();
			for (int i = 0; i < c; i++) {
				ExternalizedObject3D extChild = externalized.getChild(i);
				obj.addChild(getObject(extChild));
			}
		} finally {
			if (ois != null) {
				ois.close();
			}
		}
		return obj;
	}

	public static BaseObject3D getSerializedObject(Resources resources,
			int objId, boolean b) throws NotFoundException, IOException {
		GZIPInputStream gis = null;
		ObjectInputStream is = null;
		BaseObject3D obj = null;
		try {
			gis= new GZIPInputStream(
					resources.openRawResource(objId));
			is = new ObjectInputStream(gis);

			Log.d("Test", "Loading serialized object");
			ExternalizedObject3D externalized = (ExternalizedObject3D) is
					.readObject();
			obj = ModelUtils.getObject3d(externalized);
			int c = externalized.getChildrenCount();
			for (int i = 0; i < c; i++) {
				ExternalizedObject3D extChild = externalized.getChild(i);
				obj.addChild(getObject(extChild));
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			if (is != null) {
				is.close();
			}
			if (gis != null) {
				gis.close();
			}
		}
		return obj;
	}

}
