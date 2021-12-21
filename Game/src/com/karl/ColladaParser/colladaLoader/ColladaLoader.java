package com.karl.ColladaParser.colladaLoader;

import com.karl.ColladaParser.dataStructures.AnimatedModelData;
import com.karl.ColladaParser.dataStructures.AnimationData;
import com.karl.ColladaParser.dataStructures.MeshData;
import com.karl.ColladaParser.dataStructures.SkeletonData;
import com.karl.ColladaParser.dataStructures.SkinningData;
import com.karl.ColladaParser.xmlParser.XmlNode;
import com.karl.ColladaParser.xmlParser.XmlParser;
import com.karl.Engine.utils.MyFile;

public class ColladaLoader {

	public static AnimatedModelData loadColladaModel(MyFile colladaFile, int maxWeights) {
		XmlNode node = XmlParser.loadXmlFile(colladaFile);

		SkinLoader skinLoader = new SkinLoader(node.getChild("library_controllers"), maxWeights);
		SkinningData skinningData = skinLoader.extractSkinData();

		SkeletonLoader jointsLoader = new SkeletonLoader(node.getChild("library_visual_scenes"), skinningData.jointOrder);
		SkeletonData jointsData = jointsLoader.extractBoneData();

		GeometryLoader g = new GeometryLoader(node.getChild("library_geometries"), skinningData.verticesSkinData);
		MeshData meshData = g.extractModelData();

		return new AnimatedModelData(meshData, jointsData);
	}

	public static AnimationData loadColladaAnimation(MyFile colladaFile) {
		XmlNode node = XmlParser.loadXmlFile(colladaFile);
		XmlNode animNode = node.getChild("library_animations");
		XmlNode jointsNode = node.getChild("library_visual_scenes");
		AnimationLoader loader = new AnimationLoader(animNode, jointsNode);
		AnimationData animData = loader.extractAnimation();
		return animData;
	}

}
