/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.karaf.cellar.config.shell;

import org.apache.felix.gogo.commands.Option;
import org.apache.karaf.cellar.config.Constants;
import org.apache.karaf.cellar.core.Configurations;
import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.apache.karaf.cellar.core.Group;

import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;

/**
 * List cluster config command.
 */
@Command(scope = "cluster", name = "config-list", description = "List the configuration PIDs assigned to a group")
public class ListCommand extends ConfigCommandSupport {

    @Argument(index = 0, name = "group", description = "The cluster group name", required = true, multiValued = false)
    String groupName;

    @Option(name = "-m", aliases = { "--minimal" }, description = "Don't display the properties of each configuration", required = false, multiValued = false)
    boolean minimal;

    @Override
    protected Object doExecute() throws Exception {
        // check if the group exists
        Group group = groupManager.findGroupByName(groupName);
        if (group == null) {
            System.err.println("Cluster group " + groupName + " doesn't exist");
            return null;
        }

        Map<String, Properties> distributedConfigurations = clusterManager.getMap(Constants.CONFIGURATION_MAP + Configurations.SEPARATOR + groupName);

        if (distributedConfigurations != null && !distributedConfigurations.isEmpty()) {
            for (String pid : distributedConfigurations.keySet()) {
                System.out.println("----------------------------------------------------------------");
                System.out.println("Pid:            " + pid);
                if (!minimal) {
                    Properties properties = distributedConfigurations.get(pid);
                    if (properties != null) {
                        System.out.println("Properties:");
                        for (Enumeration e = properties.keys(); e.hasMoreElements();) {
                            Object key = e.nextElement();
                            System.out.println("   " + key + " = " + properties.get(key));
                        }
                    }
                }
            }
        } else System.err.println("No configuration PID found for cluster group " + groupName);

        return null;
    }

}
