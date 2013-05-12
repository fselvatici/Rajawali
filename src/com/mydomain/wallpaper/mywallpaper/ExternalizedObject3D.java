package com.mydomain.wallpaper.mywallpaper;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import rajawali.SerializedObject3D;

public class ExternalizedObject3D implements Externalizable {
	public static final long serialVersionUUID = 1;
	private String name;
	private float[] vertices;
	private float[] normals;
	private float[] textureCoords;
	private float[] colors;
	private int[] indices;
	private ExternalizedObject3D[] children;
	private int childrenCount;

	public ExternalizedObject3D() {
	}

	public ExternalizedObject3D(float[] vertices, float[] normals,
			float[] textureCoords, float[] colors, int[] indices,
			int childrenCount) {
		this.vertices = vertices;
		this.normals = normals;
		this.textureCoords = textureCoords;
		this.colors = colors;
		this.indices = indices;
		this.children = new ExternalizedObject3D[childrenCount];
		this.childrenCount = childrenCount;
	}

	public ExternalizedObject3D(SerializedObject3D ser, int childrenCount) {
		this(ser.getVertices(), ser.getNormals(), ser.getTextureCoords(), ser
				.getColors(), ser.getIndices(), childrenCount);
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		int _v = vertices.length;
		int _n = normals.length;
		int _t = textureCoords.length;
		int _c = colors.length;
		int _i = indices.length;
		int _h = childrenCount;
		// Serialize first the lenght of the arrays
		out.writeInt(_v);
		out.writeInt(_n);
		out.writeInt(_t);
		out.writeInt(_c);
		out.writeInt(_i);
		out.writeInt(_h);

		// Write the name of the object
		out.writeObject(name);

		// Serialize the arrays
		for (int i = 0; i < _v; i++) {
			out.writeFloat(vertices[i]);
		}
		for (int i = 0; i < _n; i++) {
			out.writeFloat(normals[i]);
		}
		for (int i = 0; i < _t; i++) {
			out.writeFloat(textureCoords[i]);
		}
		for (int i = 0; i < _c; i++) {
			out.writeFloat(colors[i]);
		}
		for (int i = 0; i < _i; i++) {
			out.writeInt(indices[i]);
		}
		if (_h > 0) {
			for (int i = 0; i < childrenCount; i++) {
				children[i].writeExternal(out);
			}
		}
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException {
		int _v = in.readInt();
		int _n = in.readInt();
		int _t = in.readInt();
		int _c = in.readInt();
		int _i = in.readInt();
		int _h = in.readInt();

		this.vertices = new float[_v];
		this.normals = new float[_n];
		this.textureCoords = new float[_t];
		this.colors = new float[_c];
		this.indices = new int[_i];
		this.childrenCount = _h;

		// Write the name of the object
		name = (String) in.readObject();

		// Serialize the arrays
		for (int i = 0; i < _v; i++) {
			vertices[i] = in.readFloat();
		}
		for (int i = 0; i < _n; i++) {
			normals[i] = in.readFloat();
		}
		for (int i = 0; i < _t; i++) {
			textureCoords[i] = in.readFloat();
		}
		for (int i = 0; i < _c; i++) {
			colors[i] = in.readFloat();
		}
		for (int i = 0; i < _i; i++) {
			indices[i] = in.readInt();
		}
		if (_h > 0) {
			this.children = new ExternalizedObject3D[_h];
			for (int i = 0; i < _h; i++) {
				children[i] = new ExternalizedObject3D();
				children[i].readExternal(in);
			}
		}
	}

	public void addChild(ExternalizedObject3D exportToSerialized, int childNum) {
		children[childNum] = exportToSerialized;
	}

	public void setName(String name) {
		this.name = name;
	}

	public float[] getVertices() {
		return vertices;
	}

	public void setVertices(float[] vertices) {
		this.vertices = vertices;
	}

	public float[] getNormals() {
		return normals;
	}

	public void setNormals(float[] normals) {
		this.normals = normals;
	}

	public float[] getTextureCoords() {
		return textureCoords;
	}

	public void setTextureCoords(float[] textureCoords) {
		this.textureCoords = textureCoords;
	}

	public float[] getColors() {
		return colors;
	}

	public void setColors(float[] colors) {
		this.colors = colors;
	}

	public int[] getIndices() {
		return indices;
	}

	public void setIndices(int[] indices) {
		this.indices = indices;
	}

	public String getName() {
		return name;
	}

	public int getChildrenCount() {
		return childrenCount;
	}

	public ExternalizedObject3D getChild(int i) {
		return children[i];
	}

}
