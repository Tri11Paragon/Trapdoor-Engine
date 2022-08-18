
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
#include "data/sql.h"

void TD::SQLTransaction::insertEntity(const TD::dPtr<TD::Entity>& e) {
    if (!firstE)
        createEntityTable();
    flog << "Not implemented";
}

void TD::SQLTransaction::insertOrUpdateEntity(const TD::dPtr<TD::Entity>& e) {
    if (!firstE)
        createEntityTable();
    flog << "Not implemented";
}

void TD::SQLTransaction::insertComponent(const TD::dPtr<TD::Component>& c) {
    if (!firstC)
        createComponentTable();
    flog << "Not implemented";
}

void TD::SQLTransaction::insertOrUpdateComponent(const TD::dPtr<TD::Component>& c) {
    if (!firstC)
        createComponentTable();
    flog << "Not implemented";
}

void TD::SQLTransaction::insert(const std::string& str) {
    strStream << str;
}

void TD::SQLTransaction::createEntityTable() {
    insert("CREATE TABLE IF NOT EXISTS entities (name MEDIUMTEXT PRIMARY KEY);");
    firstE = false;
}

void TD::SQLTransaction::createComponentTable() {
    insert("CREATE TABLE IF NOT EXISTS components (entityName MEDIUMTEXT PRIMARY KEY, componentName MEDIUMTEXT, componentTable MEDIUMTEXT);")
    firstC = false;
}
