// Copyright 2022 Chaos Mesh Authors.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// See the License for the specific language governing permissions and
// limitations under the License.

package org.chaos_mesh.agent_installer;

/**
 * Auxiliary class used by Install to provide clients with ids and display names of attachable
 * JVMs.
 */
public class VMInfo
{
    private String id;
    private String displayName;
    public VMInfo(String id, String displayName)
    {
        this.id =id;
        this.displayName = displayName;
    }
    public String getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }
}
