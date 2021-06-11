package me.ender.core.misc;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class LocationDataType implements PersistentDataType<byte[], Location> {

    @Override
    public @NotNull Class<byte[]> getPrimitiveType() {
        return byte[].class;
    }

    @Override
    public @NotNull Class<Location> getComplexType() {
        return Location.class;
    }

    @Override
    public byte @NotNull [] toPrimitive(@NotNull Location location, @NotNull PersistentDataAdapterContext persistentDataAdapterContext) {
        var len = location.getWorld().getName().getBytes(StandardCharsets.UTF_8);
        ByteBuffer bb = ByteBuffer.allocate(36+len.length);
        bb.putInt(len.length);
        bb.put(len);
        bb.putDouble(location.getX());
        bb.putDouble(location.getY());
        bb.putDouble(location.getZ());
        bb.putFloat(location.getYaw());
        bb.putFloat(location.getPitch());
        return bb.array();
    }

    @Override
    public @NotNull Location fromPrimitive(byte @NotNull [] bytes, @NotNull PersistentDataAdapterContext persistentDataAdapterContext) {
        ByteBuffer bb = ByteBuffer.wrap(bytes);
        var index = bb.getInt();
        var b = new byte[index];
        bb.get(b);
        var world = new String(b, StandardCharsets.UTF_8);
        //bb.position(index);
        var x =bb.getDouble();
        var y =bb.getDouble();
        var z =bb.getDouble();
        var yaw = bb.getFloat();
        var pitch = bb.getFloat();
        return new Location(Bukkit.getWorld(world), x,y,z,yaw,pitch);
    }

}
