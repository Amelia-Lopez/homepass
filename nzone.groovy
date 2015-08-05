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

// basic auth header value, i.e. base64 encoding of: username:password
basicAuth = ''

routerIpAddress = '192.168.0.1'

// read the file into a list
macList = []
new File('mac_list').eachLine { macList << it }

// get a random MAC address and clean up the string
mac = macList[new Random().nextInt(macList.size())].trim().toUpperCase()
println("Mac: $mac")

// URL Encode the MAC address
mac = mac.replaceAll(':', '%3A')

command = [
    'curl', 'http://' + routerIpAddress + '/apply.cgi',
    '-H', 'Origin: http://' + routerIpAddress,
    '-H', 'Referer: http://' + routerIpAddress + '/Diagnostics.asp',
    '-H', 'Authorization: Basic ' + basicAuth,
    '-H', 'Content-Type: application/x-www-form-urlencoded',
    '-H', 'Connection: keep-alive',
    '--data',
    'submit_button=Ping&action=ApplyTake&submit_type=start&change_action=gozila_cgi' +
        '&next_page=Diagnostics.asp&ping_ip=nvram+set+wl0.1_hwaddr%3D' +
        mac +
        '%0D%0Anvram+commit%0D%0Areboot']

println command.execute().text