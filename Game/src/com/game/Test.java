package com.game;

import org.lwjgl.system.*;
import org.lwjgl.util.meshoptimizer.*;
import org.lwjgl.util.par.*;

import java.nio.*;
import java.util.*;

import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;
import static org.lwjgl.util.meshoptimizer.MeshOptimizer.*;
import static org.lwjgl.util.par.ParShapes.*;

public class Test {

    public static void main(String[] args) {
        ParShapesMesh mesh = Objects.requireNonNull(par_shapes_create_torus(32, 32, 1.0f));

        System.out.println("BEFORE:");
        System.out.println("-------");
        printStats(mesh);

        IntBuffer indexBuffer = mesh.triangles(mesh.ntriangles() * 3);

        FloatBuffer vertexBuffer = mesh.points(mesh.npoints() * 3);
        FloatBuffer normalBuffer = Objects.requireNonNull(mesh.normals(mesh.npoints() * 3));

        MeshoptStream.Buffer streams = MeshoptStream.create(2)
            .apply(0, it -> it
                .data(memByteBuffer(vertexBuffer))
                .size(4 * 3)
                .stride(4 * 3))
            .apply(1, it -> it
                .data(memByteBuffer(vertexBuffer))
                .size(4 * 3)
                .stride(4 * 3));

        IntBuffer remap = memAllocInt(mesh.npoints());
        System.out.println(mesh.npoints());
        System.out.println(mesh.npoints() * 3);
        System.out.println(mesh.ntriangles());
        System.out.println(mesh.ntriangles() / 3);

        int uniqueVertices = (int)meshopt_generateVertexRemapMulti(remap, indexBuffer, indexBuffer.remaining(), streams);
        remap(vertexBuffer, indexBuffer, normalBuffer, remap);

        if (uniqueVertices < remap.remaining()) {
            remap.limit(uniqueVertices);

            vertexBuffer.limit(uniqueVertices * 3);
            normalBuffer.limit(uniqueVertices * 3);
        }

        meshopt_optimizeVertexCache(indexBuffer, indexBuffer, uniqueVertices);
        meshopt_optimizeOverdraw(indexBuffer, indexBuffer, vertexBuffer, uniqueVertices, 3 * Float.BYTES, 1.05f);

        assert (int)meshopt_optimizeVertexFetchRemap(remap, indexBuffer) == uniqueVertices;
        remap(vertexBuffer, indexBuffer, normalBuffer, remap);

        memFree(remap);

        memPutInt(mesh.address() + ParShapesMesh.NPOINTS, uniqueVertices);

// FOLLOWING seems to be superseded by assimp
	    
//	    // 3 floats per vertex
//	    int vertex_count = vertices.size() / 3;
//	    // 3 vertices per face
//	    int face_count = vertex_count / 3;
//	    int index_count = face_count * 3;
//	    
//	    IntBuffer remap = MemoryUtil.memAllocInt(index_count);
//	    
//	    MeshoptStream.Buffer streams = MeshoptStream.create(3)
//	            .apply(0, it -> it
//	                .data(memByteBuffer(vertexBuffer))
//	                .size(4 * 3)
//	                .stride(4 * 3))
//	            .apply(1, it -> it
//	                .data(memByteBuffer(normalBuffer))
//	                .size(4 * 3)
//	                .stride(4 * 3))
//	            .apply(2, it -> it
//		                .data(memByteBuffer(textureBuffer))
//		                .size(4 * 2)
//		                .stride(4 * 2));
//	    
//	    
//	    int uniqueVertices = (int) MeshOptimizer.meshopt_generateVertexRemapMulti(remap, indexBuffer, indexBuffer.remaining(), streams);
//
//	    
//	    MeshOptimizer.meshopt_remapIndexBuffer(indexBuffer, indexBuffer, remap);
//	    MeshOptimizer.meshopt_remapVertexBuffer(MemoryUtil.memByteBuffer(vertexBuffer), MemoryUtil.memByteBuffer(vertexBuffer), Float.BYTES * 3, remap);
//	    MeshOptimizer.meshopt_remapVertexBuffer(MemoryUtil.memByteBuffer(normalBuffer), MemoryUtil.memByteBuffer(normalBuffer), Float.BYTES * 3, remap);
//	    MeshOptimizer.meshopt_remapVertexBuffer(MemoryUtil.memByteBuffer(textureBuffer), MemoryUtil.memByteBuffer(textureBuffer), Float.BYTES * 2, remap);
//
//	    if (uniqueVertices < remap.remaining()) {
//	    	remap.limit(uniqueVertices);
//	    	vertexBuffer.limit(uniqueVertices * 3);
//	    	textureBuffer.limit(uniqueVertices * 2);
//	    	normalBuffer.limit(uniqueVertices * 3);
//	    }
//	    
//	    MeshOptimizer.meshopt_optimizeVertexCache(indexBuffer, indexBuffer, uniqueVertices);
//	    MeshOptimizer.meshopt_optimizeOverdraw(indexBuffer, indexBuffer, vertexBuffer, uniqueVertices, 3 * Float.BYTES, 1.05f);
//	    
//	    assert (int)MeshOptimizer.meshopt_optimizeVertexFetchRemap(remap, indexBuffer) == uniqueVertices;
//	    
//	    MeshOptimizer.meshopt_remapIndexBuffer(indexBuffer, indexBuffer, remap);
//	    MeshOptimizer.meshopt_remapVertexBuffer(MemoryUtil.memByteBuffer(vertexBuffer), MemoryUtil.memByteBuffer(vertexBuffer), Float.BYTES * 3, remap);
//	    MeshOptimizer.meshopt_remapVertexBuffer(MemoryUtil.memByteBuffer(normalBuffer), MemoryUtil.memByteBuffer(normalBuffer), Float.BYTES * 3, remap);
//	    MeshOptimizer.meshopt_remapVertexBuffer(MemoryUtil.memByteBuffer(textureBuffer), MemoryUtil.memByteBuffer(textureBuffer), Float.BYTES * 2, remap);
	    
	    //MemoryUtil.memFree(remap);
        
        System.out.println("\nAFTER:");
        System.out.println("------");
        printStats(mesh);
    }

    private static void remap(FloatBuffer vertexBuffer, IntBuffer indexBuffer, FloatBuffer normalBuffer, IntBuffer remap) {
        meshopt_remapIndexBuffer(indexBuffer, indexBuffer, remap);
        meshopt_remapVertexBuffer(memByteBuffer(vertexBuffer), memByteBuffer(vertexBuffer), 3 * Float.BYTES, remap);
        meshopt_remapVertexBuffer(memByteBuffer(normalBuffer), memByteBuffer(normalBuffer), 3 * Float.BYTES, remap);
    }

    private static void printStats(ParShapesMesh mesh) {
        try (MemoryStack stack = stackPush()) {
            MeshoptVertexCacheStatistics stats = meshopt_analyzeVertexCache(
                mesh.triangles(mesh.ntriangles() * 3),
                mesh.npoints(),
                16, 0, 0,
                MeshoptVertexCacheStatistics.malloc(stack)
            );
            System.out.println("ACMR: " + stats.acmr());
            System.out.println("ATVR: " + stats.atvr());
        }
    }

}
