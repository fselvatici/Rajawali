package com.tixis.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.zip.GZIPOutputStream;

import rajawali.BaseObject3D;
import rajawali.Geometry3D;
import rajawali.SerializedObject3D;
import rajawali.animation.mesh.VertexAnimationFrame;
import rajawali.animation.mesh.VertexAnimationObject3D;
import rajawali.materials.TextureManager;
import rajawali.parser.ObjParser;
import rajawali.util.RajLog;
import android.content.Context;
import android.os.Environment;

import com.mydomain.wallpaper.mywallpaper.ExternalizedObject3D;

public class MeshExporter {

	private BaseObject3D mObject;
	private String mFileName;
	private boolean mCompressed;
	private File mExportDir = null;
	private String mFullFileName;

	public enum ExportType {
		SERIALIZED, OBJ
	}

	public MeshExporter(BaseObject3D objectToExport) {
		mObject = objectToExport;
	}

	public void setExportDirectory(File exportDir) {
		// The path's validity is the user's responsibility.
		// Any problems are taken care of by the try blocks in the private
		// export methods.
		mExportDir = exportDir;
	}

	public void export(String fileName, ExportType type) {
		export(fileName, type, false);
	}

	public void export(String fileName, ExportType type, boolean compressed) {
		mFileName = fileName;
		mCompressed = compressed;
		switch (type) {
		case SERIALIZED:
			ExternalizedObject3D ext = exportToExternalized(mObject);
			saveToFile(ext);
			break;
		case OBJ:
			exportToObj();
			break;
		}
	}

	private File getExportFile() {
		File path;
		if (mExportDir == null)
			path = Environment.getExternalStorageDirectory();
		else
			path = mExportDir;
		return new File(path, mFileName);

	}

	private void exportToObj() {
		RajLog.d("Exporting " + mObject.getName() + " as .obj file");
		Geometry3D g = mObject.getGeometry();
		StringBuffer sb = new StringBuffer();

		sb.append("# Exported by Rajawali 3D Engine for Android\n");
		sb.append("o ");
		sb.append(mObject.getName());
		sb.append("\n");

		for (int i = 0; i < g.getVertices().capacity(); i += 3) {
			sb.append("v ");
			sb.append(g.getVertices().get(i));
			sb.append(" ");
			sb.append(g.getVertices().get(i + 1));
			sb.append(" ");
			sb.append(g.getVertices().get(i + 2));
			sb.append("\n");
		}

		sb.append("\n");

		for (int i = 0; i < g.getTextureCoords().capacity(); i += 2) {
			sb.append("vt ");
			sb.append(g.getTextureCoords().get(i));
			sb.append(" ");
			sb.append(g.getTextureCoords().get(i + 1));
			sb.append("\n");
		}

		sb.append("\n");

		for (int i = 0; i < g.getNormals().capacity(); i += 3) {
			sb.append("vn ");
			sb.append(g.getNormals().get(i));
			sb.append(" ");
			sb.append(g.getNormals().get(i + 1));
			sb.append(" ");
			sb.append(g.getNormals().get(i + 2));
			sb.append("\n");
		}

		sb.append("\n");

		boolean isIntBuffer = g.getIndices() instanceof IntBuffer;

		for (int i = 0; i < g.getIndices().capacity(); i++) {
			if (i % 3 == 0)
				sb.append("\nf ");
			int index = isIntBuffer ? ((IntBuffer) g.getIndices()).get(i) + 1
					: ((ShortBuffer) g.getIndices()).get(i) + 1;
			sb.append(index);
			sb.append("/");
			sb.append(index);
			sb.append("/");
			sb.append(index);
			sb.append(" ");
		}

		try {

			File f = getExportFile();
			FileWriter writer = new FileWriter(f);
			writer.append(sb.toString());
			writer.flush();
			writer.close();

			RajLog.d(".obj export successful: " + f.getCanonicalPath());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Make sure this line is in your AndroidManifer.xml file, under <manifest>:
	 * <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"
	 * />
	 * 
	 * @param obj
	 */
	private ExternalizedObject3D exportToExternalized(BaseObject3D obj) {
		ExternalizedObject3D ext = null;
		try {

			int count = obj.getNumChildren();

			SerializedObject3D ser = obj.toSerializedObject3D();
			ext = new ExternalizedObject3D(ser, count);
			ext.setName(obj.getName());
			if (count > 0) {
				for (int i = 0; i < count; i++) {
					BaseObject3D child = obj.getChildAt(i);
					ext.addChild(exportToExternalized(child), i);
				}
			}

			if (obj instanceof VertexAnimationObject3D) {
				VertexAnimationObject3D o = (VertexAnimationObject3D) obj;
				int numFrames = o.getNumFrames();
				float[][] vs = new float[numFrames][];
				float[][] ns = new float[numFrames][];
				String[] frameNames = new String[numFrames];

				for (int i = 0; i < numFrames; ++i) {
					VertexAnimationFrame frame = (VertexAnimationFrame) o
							.getFrame(i);
					Geometry3D geom = frame.getGeometry();
					float[] v = new float[geom.getVertices().limit()];
					geom.getVertices().get(v);
					float[] n = new float[geom.getNormals().limit()];
					geom.getNormals().get(n);
					vs[i] = v;
					ns[i] = n;
					frameNames[i] = frame.getName();
				}

				ser.setFrameVertices(vs);
				ser.setFrameNormals(ns);
				ser.setFrameNames(frameNames);
			}

		} catch (Exception e) {
			RajLog.e("Serializing " + mFileName + " was unsuccessfull.");
			e.printStackTrace();
		}
		return ext;
	}

	public static void serializeObj(Context context,
			TextureManager textureManager, int resourceId, String outputName) {
		serializeObj(context, textureManager, resourceId, outputName, false,
				null);
	}

	public static void serializeObj(Context context,
			TextureManager textureManager, int resourceId, String outputName,
			Boolean compress) {
		serializeObj(context, textureManager, resourceId, outputName, compress,
				null);
	}

	public static void serializeObj(Context context,
			TextureManager textureManager, int resourceId, String outputName,
			File exportDir) {
		serializeObj(context, textureManager, resourceId, outputName, false,
				exportDir);
	}

	public static void serializeObj(Context context,
			TextureManager textureManager, int resourceId, String outputName,
			Boolean compress, File exportDir) {
		final ObjParser objParser = new ObjParser(context.getResources(),
				textureManager, resourceId);
		try {
			objParser.parse();
			final BaseObject3D obj = objParser.getParsedObject();
			final MeshExporter exporter = new MeshExporter(obj);
			exporter.setExportDirectory(exportDir);
			exporter.export(outputName, ExportType.SERIALIZED, compress);
		} catch (Exception e) {
			RajLog.e("Failed to serialize obj: " + e.getMessage());
			e.printStackTrace();
		}
	}

	private void saveToFile(ExternalizedObject3D ext) {
		FileOutputStream fos;
		ObjectOutputStream os = null;
		String sdcardPath = "";
		try {
			File sdcardStorage = Environment.getExternalStorageDirectory();
			sdcardPath = sdcardStorage.getParent() + java.io.File.separator
					+ sdcardStorage.getName();
			String fn = mFileName;
			mFullFileName = sdcardPath + File.separator + fn;
			File f = new File(mFullFileName);
			fos = new FileOutputStream(f);
			if (mCompressed) {
				GZIPOutputStream gz = new GZIPOutputStream(fos);
				os = new ObjectOutputStream(gz);
			} else {
				os = new ObjectOutputStream(fos);
			}
			os.writeObject(ext);
			os.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		RajLog.i("Successfully serialized " + mFileName + " to SD card.("
				+ sdcardPath + ")");

	}

	public String getFullFileName() {
		return mFullFileName;
	}

	public BaseObject3D getExportedModel() throws FileNotFoundException,
			IOException, ClassNotFoundException {
		MeshImporter importer = new MeshImporter();
		// BaseObject3D model = importer.importObjects(new FileInputStream(new
		// File(mFullFileName)));
		// return model;
		return null;
	}

}