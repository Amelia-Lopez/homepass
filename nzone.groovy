#!/usr/bin/env groovy

/*
 * Copyright 2015 Mario Lopez Jr
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

@Grapes(
    @Grab('org.yaml:snakeyaml:1.15')
)

import org.yaml.snakeyaml.Yaml

// open config file
configFileName = 'config.yaml'
configFile = new File(configFileName)
if (!configFile.exists()) {
    println "Expected config file did not exist: $configFileName"
    System.exit(1)
}

// read config file
Yaml yaml = new Yaml()
Map<String, String> config = yaml.load(new FileInputStream(configFile))

// get config values from the yaml
basicAuth = config['basicAuth']  // base64 encoding of: username:password
routerIpAddress = config['routerIpAddress'] // e.g. 192.168.0.1
macListFile = config['macListFile'] // file containing the list of MAC addresses
routerWifiInterface = config['routerWifiInterface'] // wifi interface to update, e.g. wl0.1 = first virtual interface

// read the MAC file into a list
macList = []
new File(macListFile).eachLine { macList << it }

// get a random MAC address and clean up the string
mac = macList[new Random().nextInt(macList.size())].trim().toUpperCase()
println("Mac: $mac")

// command that will be sent to the router
routerCommand = """\
nvram set ${routerWifiInterface}_hwaddr=$mac
nvram commit
reboot"""

// URL-encode the command so it can be sent over HTTP
routerCommand = java.net.URLEncoder.encode(routerCommand)

curlCommand = [
    'curl', 'http://' + routerIpAddress + '/apply.cgi',
    '-H', 'Origin: http://' + routerIpAddress,
    '-H', 'Referer: http://' + routerIpAddress + '/Diagnostics.asp',
    '-H', 'Authorization: Basic ' + basicAuth,
    '-H', 'Content-Type: application/x-www-form-urlencoded',
    '-H', 'Connection: keep-alive',
    '--data', 'submit_button=Ping&action=ApplyTake&submit_type=start&change_action=gozila_cgi' +
        '&next_page=Diagnostics.asp&ping_ip=' + routerCommand]

println curlCommand.execute().text