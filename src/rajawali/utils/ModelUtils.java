package rajawali.utils;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.StreamCorruptedException;
import java.util.HashMap;
import java.util.Map;

import rajawali.BaseObject3D;
import rajawali.SerializedObject3D;
import rajawali.lights.ALight;
import rajawali.materials.DiffuseMaterial;
import rajawali.materials.TextureManager;
import rajawali.parser.ObjParser;
import rajawali.util.MeshExporter;
import rajawali.util.MeshExporter.ExportType;
import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.mydomain.wallpaper.mywallpaper.ExternalizedObject3D;

public class ModelUtils {


	public static SerializedObject3D getSerialized(Resources resources, int objId)
			throws StreamCorruptedException, NotFoundException, IOException, ClassNotFoundException {
		ObjectInputStream ois = new ObjectInputStream(resources.openRawResource(objId));
		SerializedObject3D serialized = (SerializedObject3D) ois.readObject();
		ois.close();
		return serialized;
	}

	public static BaseObject3D getObject3d(ExternalizedObject3D externalized) {
		BaseObject3D obj = new BaseObject3D();
		obj.setData(externalized.getVertices(), externalized.getNormals(),
				externalized.getTextureCoords(), externalized.getColors(),
				externalized.getIndices());
		obj.setName(externalized.getName());
		int c= externalized.getChildrenCount();
		for(int i=0;i<c;i++) {
			ExternalizedObject3D extChild = externalized.getChild(i);
			obj.addChild(getObject3d(extChild));
		}
		return obj;
	}
}
