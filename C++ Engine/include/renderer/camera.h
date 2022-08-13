//
// Created by brett on 23/07/22.
//

#ifndef ENGINE_CAMERA_H
#define ENGINE_CAMERA_H

#include "../glm.h"

namespace TD {
    class camera {
    protected:
        glm::vec3 _cameraPos = glm::vec3(0.0f);
        float _moveAtX = 0, _moveAtY = 0, _moveAtZ = 0;
        float _yaw = 0, _pitch = 0, _roll = 0;
        float clippingPlanes[16];
        double m_Frustum[6][4];
        static inline float getInMatrix(glm::mat4 mat,int pos){
            return mat[pos >> 2][pos % 4];
        }
        void normalizePlane(double frustum[6][4], int side);
        void calculateFrustum();
    public:
        virtual void update() {}
        glm::vec3 getPosition();
        glm::vec3 getRotation();
        float getYaw();
        float getPitch();
        float getRoll();
        bool pointInFrustum(double x, double y, double z);
        bool sphereInFrustum(double x, double y, double z, double radius);
        bool cubeInFrustum(double x1, double y1, double z1, double x2, double y2, double z2);
        inline bool pointInFrustum(glm::vec3 pos) { return pointInFrustum(pos.x, pos.y, pos.z); }
        inline bool sphereInFrustum(glm::vec3 pos, double radius) { return sphereInFrustum(pos.x, pos.y, pos.z, radius); }
        inline bool cubeInFrustum(glm::vec3 pos1, glm::vec3 pos2) { return cubeInFrustum(pos1.x, pos1.y, pos1.z, pos2.x, pos2.y, pos2.z); }
    };

    class firstPersonCamera : public camera {
    protected:
        float speed = 0.0f;
        constexpr static const float turnSpeedY = 5.0f;
        constexpr static const float turnSpeedX = 4.5f;
    public:
        void update();
    };

    class Frustum {
    public:
        Frustum() {}

        // m = ProjectionMatrix * ViewMatrix
        Frustum(glm::mat4 m);

        // http://iquilezles.org/www/articles/frustumcorrect/frustumcorrect.htm
        bool IsBoxVisible(const glm::vec3& minp, const glm::vec3& maxp) const;

    private:
        enum Planes
        {
            Left = 0,
            Right,
            Bottom,
            Top,
            Near,
            Far,
            Count,
            Combinations = Count * (Count - 1) / 2
        };

        template<Planes i, Planes j>
        struct ij2k {
            enum { k = i * (9 - i) / 2 + j - 1 };
        };

        template<Planes a, Planes b, Planes c>
        glm::vec3 intersection(const glm::vec3* crosses) const;

        glm::vec4   m_planes[Count];
        glm::vec3   m_points[8];
    };

    inline Frustum::Frustum(glm::mat4 m) {
        m = glm::transpose(m);
        m_planes[Left]   = m[3] + m[0];
        m_planes[Right]  = m[3] - m[0];
        m_planes[Bottom] = m[3] + m[1];
        m_planes[Top]    = m[3] - m[1];
        m_planes[Near]   = m[3] + m[2];
        m_planes[Far]    = m[3] - m[2];

        glm::vec3 crosses[Combinations] = {
                glm::cross(glm::vec3(m_planes[Left]),   glm::vec3(m_planes[Right])),
                glm::cross(glm::vec3(m_planes[Left]),   glm::vec3(m_planes[Bottom])),
                glm::cross(glm::vec3(m_planes[Left]),   glm::vec3(m_planes[Top])),
                glm::cross(glm::vec3(m_planes[Left]),   glm::vec3(m_planes[Near])),
                glm::cross(glm::vec3(m_planes[Left]),   glm::vec3(m_planes[Far])),
                glm::cross(glm::vec3(m_planes[Right]),  glm::vec3(m_planes[Bottom])),
                glm::cross(glm::vec3(m_planes[Right]),  glm::vec3(m_planes[Top])),
                glm::cross(glm::vec3(m_planes[Right]),  glm::vec3(m_planes[Near])),
                glm::cross(glm::vec3(m_planes[Right]),  glm::vec3(m_planes[Far])),
                glm::cross(glm::vec3(m_planes[Bottom]), glm::vec3(m_planes[Top])),
                glm::cross(glm::vec3(m_planes[Bottom]), glm::vec3(m_planes[Near])),
                glm::cross(glm::vec3(m_planes[Bottom]), glm::vec3(m_planes[Far])),
                glm::cross(glm::vec3(m_planes[Top]),    glm::vec3(m_planes[Near])),
                glm::cross(glm::vec3(m_planes[Top]),    glm::vec3(m_planes[Far])),
                glm::cross(glm::vec3(m_planes[Near]),   glm::vec3(m_planes[Far]))
        };

        m_points[0] = intersection<Left,  Bottom, Near>(crosses);
        m_points[1] = intersection<Left,  Top,    Near>(crosses);
        m_points[2] = intersection<Right, Bottom, Near>(crosses);
        m_points[3] = intersection<Right, Top,    Near>(crosses);
        m_points[4] = intersection<Left,  Bottom, Far>(crosses);
        m_points[5] = intersection<Left,  Top,    Far>(crosses);
        m_points[6] = intersection<Right, Bottom, Far>(crosses);
        m_points[7] = intersection<Right, Top,    Far>(crosses);

    }

// http://iquilezles.org/www/articles/frustumcorrect/frustumcorrect.htm
    inline bool Frustum::IsBoxVisible(const glm::vec3& minp, const glm::vec3& maxp) const {
        // check box outside/inside of frustum
        for (int i = 0; i < Count; i++) {
            if ((glm::dot(m_planes[i], glm::vec4(minp.x, minp.y, minp.z, 1.0f)) < 0.0) &&
                (glm::dot(m_planes[i], glm::vec4(maxp.x, minp.y, minp.z, 1.0f)) < 0.0) &&
                (glm::dot(m_planes[i], glm::vec4(minp.x, maxp.y, minp.z, 1.0f)) < 0.0) &&
                (glm::dot(m_planes[i], glm::vec4(maxp.x, maxp.y, minp.z, 1.0f)) < 0.0) &&
                (glm::dot(m_planes[i], glm::vec4(minp.x, minp.y, maxp.z, 1.0f)) < 0.0) &&
                (glm::dot(m_planes[i], glm::vec4(maxp.x, minp.y, maxp.z, 1.0f)) < 0.0) &&
                (glm::dot(m_planes[i], glm::vec4(minp.x, maxp.y, maxp.z, 1.0f)) < 0.0) &&
                (glm::dot(m_planes[i], glm::vec4(maxp.x, maxp.y, maxp.z, 1.0f)) < 0.0))
            {
                return false;
            }
        }

        // check frustum outside/inside box
        int out;
        out = 0; for (int i = 0; i<8; i++) out += ((m_points[i].x > maxp.x) ? 1 : 0); if (out == 8) return false;
        out = 0; for (int i = 0; i<8; i++) out += ((m_points[i].x < minp.x) ? 1 : 0); if (out == 8) return false;
        out = 0; for (int i = 0; i<8; i++) out += ((m_points[i].y > maxp.y) ? 1 : 0); if (out == 8) return false;
        out = 0; for (int i = 0; i<8; i++) out += ((m_points[i].y < minp.y) ? 1 : 0); if (out == 8) return false;
        out = 0; for (int i = 0; i<8; i++) out += ((m_points[i].z > maxp.z) ? 1 : 0); if (out == 8) return false;
        out = 0; for (int i = 0; i<8; i++) out += ((m_points[i].z < minp.z) ? 1 : 0); if (out == 8) return false;

        return true;
    }

    template<Frustum::Planes a, Frustum::Planes b, Frustum::Planes c>
    inline glm::vec3 Frustum::intersection(const glm::vec3* crosses) const {
        float D = glm::dot(glm::vec3(m_planes[a]), crosses[ij2k<b, c>::k]);
        glm::vec3 res = glm::mat3(crosses[ij2k<b, c>::k], -crosses[ij2k<a, c>::k], crosses[ij2k<a, b>::k]) *
                        glm::vec3(m_planes[a].w, m_planes[b].w, m_planes[c].w);
        return res * (-1.0f / D);
    }
}

#endif //ENGINE_CAMERA_H
