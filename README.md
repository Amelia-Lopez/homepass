Homepass Scripts
================

Nintendo Zone
-------------
`nzone.groovy`  
  
This script will run a curl command hitting a router running DD-WRT that will change the MAC address (i.e. BSSID) of the first virtual interface to a random MAC address that is commonly used for homepass.  
  
### Usage
1. Create a config.yaml file using the config_sample.yaml template with your specific values.
2. Run the script:
```console
./nzone.groovy
```
  
### Config
* basicAuth - your router's login credentials, base64 encoding of: username:password
* routerIpAddress - the IP address of your router
* macListFile - the file name of the file containing a list of MAC addresses (use the value in the sample config if you're not sure)
* routerWifiInterface - the wireless interface on the router to update, e.g. "wl0.1" is usually the first virtual interface
  
### Details
The script automates the following manual process:  
  
1. Randomly choosing a MAC address from a list of known addesses typically used for homepass (e.g. 00:0D:67:15:D5:44).
2. Logging into the DD-WRT web interface.
3. Going to **Administration** -> **Commands**.
4. Running the following commands:
```console
nvram set wl0.1_hwaddr=00:0D:67:15:D5:44
nvram commit
reboot
```
  
Some routers will let you run something like (without requiring a reboot):  
```console
ifconfig wl0.1 down hw ether 00:0D:67:15:D5:44 up

# or:
wl -i wl0.1 down
wl -i wl0.1 bssid 00:0D:67:15:D5:44
wl -i wl0.1 up
```

My router (Netgear R7000 running DD-WRT v3.0-r27525M (07/15/15) kongac) does not, so I have to update the MAC address in the nvram and then reboot the router for it to take effect.  Other folks have come up with alternative solutions for this shortcoming:  
[https://www.reddit.com/r/3DS/comments/1k0g58/setting_up_a_streetpass_relay_at_home/cbopp78](https://www.reddit.com/r/3DS/comments/1k0g58/setting_up_a_streetpass_relay_at_home/cbopp78)