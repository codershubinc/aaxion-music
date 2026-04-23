package com.codershubinc.aaxion_music.utils

import android.content.Context
import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import android.util.Log

data class AaxionServiceInfo(
    val name: String,
    val host: String,
    val port: Int,
    val description: String = "",
    val deviceId: String = "",
    val deviceName: String = "",
    val fullInfo: String = ""
)

class NetworkDiscovery(context: Context) {
    private val nsdManager = context.getSystemService(Context.NSD_SERVICE) as NsdManager
    private val SERVICE_TYPE = "_aaxion._tcp."

    private var discoveryListener: NsdManager.DiscoveryListener? = null

    fun discoverServices(
        onServiceFound: (AaxionServiceInfo) -> Unit,
        onDiscoveryStarted: () -> Unit = {},
        onDiscoveryStopped: () -> Unit = {},
        onDiscoveryLost: (String) -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        stopDiscovery()

        discoveryListener = object : NsdManager.DiscoveryListener {
            override fun onDiscoveryStarted(regType: String) {
                Log.d("NSD", "Service discovery started")
                onDiscoveryStarted()
            }

            override fun onServiceFound(service: NsdServiceInfo) {
                Log.d("NSD", "Service found: ${service.serviceName}")
                if (service.serviceType.contains(SERVICE_TYPE)) {
                    nsdManager.resolveService(service, object : NsdManager.ResolveListener {
                        override fun onResolveFailed(serviceInfo: NsdServiceInfo, errorCode: Int) {
                            Log.e("NSD", "Resolve failed: $errorCode")
                            onError("Failed to resolve service: $errorCode")
                        }

                        override fun onServiceResolved(serviceInfo: NsdServiceInfo) {
                            Log.d("NSD", "Resolve Succeeded: $serviceInfo")
                            
                            val attributes = serviceInfo.attributes
                            val description = attributes["description"]?.let { String(it) } ?: ""
                            val deviceId = attributes["device_id"]?.let { String(it) } ?: ""
                            val deviceName = attributes["device_name"]?.let { String(it) } ?: ""

                            val info = AaxionServiceInfo(
                                name = serviceInfo.serviceName,
                                host = serviceInfo.host.hostAddress ?: "Unknown",
                                port = serviceInfo.port,
                                description = description,
                                deviceId = deviceId,
                                deviceName = deviceName,
                                fullInfo = serviceInfo.toString()
                            )
                            onServiceFound(info)
                        }
                    })
                }
            }

            override fun onServiceLost(service: NsdServiceInfo) {
                Log.e("NSD", "service lost: $service")
                onDiscoveryLost("Service lost: ${service.serviceName}")
            }

            override fun onDiscoveryStopped(serviceType: String) {
                Log.i("NSD", "Discovery stopped: $serviceType")
                onDiscoveryStopped()
            }

            override fun onStartDiscoveryFailed(serviceType: String, errorCode: Int) {
                Log.e("NSD", "Discovery failed: Error code:$errorCode")
                onError("Discovery failed to start: $errorCode")
                nsdManager.stopServiceDiscovery(this)
            }

            override fun onStopDiscoveryFailed(serviceType: String, errorCode: Int) {
                Log.e("NSD", "Discovery failed: Error code:$errorCode")
                onError("Discovery failed to stop: $errorCode")
                nsdManager.stopServiceDiscovery(this)
            }
          
        }

        nsdManager.discoverServices(SERVICE_TYPE, NsdManager.PROTOCOL_DNS_SD, discoveryListener)
    }

    fun stopDiscovery() {
        discoveryListener?.let {
            try {
                nsdManager.stopServiceDiscovery(it)
            } catch (e: Exception) {
                Log.e("NSD", "Error stopping discovery", e)
            }
            discoveryListener = null
        }
    }
}
