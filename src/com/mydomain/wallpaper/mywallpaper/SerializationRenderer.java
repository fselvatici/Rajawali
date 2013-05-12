package com.mydomain.wallpaper.mywallpaper;

import java.io.IOException;
import java.io.StreamCorruptedException;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import rajawali.BaseObject3D;
import rajawali.lights.PointLight;
import rajawali.materials.AMaterial;
import rajawali.materials.SimpleMaterial;
import rajawali.math.Number3D;
import rajawali.parser.AParser.ParsingException;
import rajawali.renderer.RajawaliRenderer;
import android.content.Context;
import android.content.res.Resources.NotFoundException;
import android.os.Debug;
import android.text.format.Time;
import android.util.Log;

import com.tixis.utils.MeshExporter;
import com.tixis.utils.MeshExporter.ExportType;
import com.tixis.utils.MeshImporter;

public class SerializationRenderer extends RajawaliRenderer {
	private BaseObject3D obj;
	private PointLight mLight;
	private AMaterial material;
	private boolean loadObjObject = true;

	public SerializationRenderer(Context context) {
		super(context);
	}

	protected void initScene() {
		getCurrentCamera().setLookAt(0, 0, 0);
		getCurrentCamera().setZ(-6);

		try {
			Time now = new Time();
			addLights();

			now.setToNow();
			Log.d("PROFILE", "Time start: " + now.toMillis(true));
			if (loadObjObject) {
				// Import the wavefront object
				obj = MeshImporter.getObject(mContext.getResources(),
						mTextureManager, R.raw.toyplane);

				// Serialize it in externalizable format
				MeshExporter exporter = new MeshExporter(obj);
				exporter.export("model_ext", ExportType.SERIALIZED, true);
				now.setToNow();
				Log.d("PROFILE", "Time exported (ext): " + now.toMillis(true));

			} else {
				// Import the serialized object
				obj = MeshImporter.getSerializedObject(mContext.getResources(),
						R.raw.toyplane_ser, true);
				material = new SimpleMaterial();
				int l = obj.getNumChildren();
				for (int i = 0; i < l; i++) {
					BaseObject3D child = obj.getChildAt(i);
					Log.i("PROFILE", "Setting material to child: " + (i + 1)
							+ ".-" + child.getName());
					child.setMaterial(material);
					child.setColor(new Number3D(1, 1, 1));
				}
				obj.setMaterial(material);
				obj.setColor(new Number3D(1, 1, 1));
				now.setToNow();
				Log.d("PROFILE", "Time imported: " + now.toMillis(true));
			}

			obj.setScale(.1f);
			obj.addLight(mLight);
			// obj.setY(-1);
			obj.setRotation(0, 90, 45);
			addChild(obj);
			now.setToNow();
			Log.d("PROFILE", "Time added to the scene: " + now.toMillis(true));
		} catch (ParsingException e) {
			e.printStackTrace();
		} catch (StreamCorruptedException e) {
			e.printStackTrace();
		} catch (NotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void addLights() {
		mLight = new PointLight();
		mLight.setPosition(0, 1, -6);
		mLight.setPower(3);
	}

	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		super.onSurfaceCreated(gl, config);
	}

	public void onDrawFrame(GL10 glUnused) {
		super.onDrawFrame(glUnused);
	}
}
