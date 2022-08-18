/*
 * Trapdoor Game Engine.
 * Copyright (C)  2022.  Brett "Tri11Paragon"
 *
 * License is provided by the LICENSE.MD "LICENSE.md" file in the base directory, otherwise:
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

//
// Created by brett on 13/08/22.
//

#ifndef ENGINE_SQL_H
#define ENGINE_SQL_H

#include <logging.h>
#include <sqlite/sqlite3.h>
#include <std.h>
#include <world/World.h>

namespace TD {

    class SQLTransaction {
        private:
            std::stringstream strStream;
        public:
            SQLTransaction() { strStream << "BEGIN TRANSACTION;"; }

            void insertEntity(const dPtr<Entity>& e);
            void insertOrUpdateEntity(const dPtr<Entity>& e);
            void insertComponent(const dPtr<Component>& c);
            void insertOrUpdateComponent();

            std::string get(){strStream << "COMMIT;"; return strStream.str();}
    };

}

#endif //ENGINE_SQL_H
