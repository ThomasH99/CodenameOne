package com.diamonddevgroup.device;


/**
 *  @deprecated internal implementation detail
 *  @author Diamond
 */
public interface DeviceNative extends com.codename1.system.NativeInterface {

	public String manufacturer();

	public String name();

	public String model();

	public boolean isNotch();
}
