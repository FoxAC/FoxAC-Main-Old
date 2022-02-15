package dev.isnow.fox.check.impl.player.crasher;

import dev.isnow.fox.check.Check;
import dev.isnow.fox.check.api.CheckInfo;
import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.packet.Packet;
import dev.isnow.fox.util.CrasherUtils;
import io.github.retrooper.packetevents.packetwrappers.play.in.blockplace.WrappedPacketInBlockPlace;
import io.github.retrooper.packetevents.packetwrappers.play.in.custompayload.WrappedPacketInCustomPayload;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;

@CheckInfo(name = "Crasher", description = "Checks for common exploits with books & payloads.", type = "G")
public class CrasherG extends Check {
    public CrasherG(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if(packet.isCustomPayload()) {
            WrappedPacketInCustomPayload wrappedPacketInCustomPayload = new WrappedPacketInCustomPayload(packet.getRawPacket());

            ByteBuf byteBuf = wrappedPacketInCustomPayload.readObject(0, ByteBuf.class);

            String channel = wrappedPacketInCustomPayload.getChannelName();

            if (byteBuf.capacity() == 0) {
                packet.getProcessor().setCancelled(true);
                fail("invalid byteBuf.");
            }

            if (channel == null || channel.isEmpty()) {
                packet.getProcessor().setCancelled(true);
                fail("Invalid channel name.");
            }

            if (channel.equals("MC|BSign") || channel.equals("MC|BEdit") || channel.equals("MC|BOpen")) {
                if (!data.getPlayer().getItemInHand().getType().toString().contains("BOOK")) {
                    fail("Sending Bedit|BSign|BOpen without holding a book");
                    packet.getProcessor().setCancelled(true);
                }
            }

            try {
                net.minecraft.server.v1_8_R3.ItemStack stack = null;
                short short0 = byteBuf.readShort();
                if (short0 >= 0) {
                    byte b0 = byteBuf.readByte();
                    short short1 = byteBuf.readShort();
                    stack = new net.minecraft.server.v1_8_R3.ItemStack(Item.getById(short0), b0, short1);

                    NBTTagCompound tag;

                    int i = byteBuf.readerIndex();
                    byte b1 = byteBuf.readByte();
                    if (b1 == 0) {
                        tag = null;
                    } else {
                        byteBuf.readerIndex(i);
                        tag = NBTCompressedStreamTools.a(new ByteBufInputStream(byteBuf), new NBTReadLimiter(2097152L));
                    }

                    stack.setTag(tag);
                }
                if (channel.equals("MC|BEdit") && !ItemBookAndQuill.b(stack.getTag())) {
                    fail("Invalid book arguments");
                } else if (channel.equals("MC|BSign") && !ItemWrittenBook.b(stack.getTag())) {
                    fail("Invalid writtenbook arguments");
                }

                if (CrasherUtils.isInvalidBookTag(stack.getTag())) {
                    fail("Invalid Book Tag.");
                }

                try {
                    if (channel.equals("MC|BSign") && !stack.getTag().getString("author").equals(data.getPlayer().getName())) {
                        fail("Sign book with another author (author: " + stack.getTag().getString("author") + ")");
                        packet.getProcessor().setCancelled(true);
                    }
                } catch (Exception e) {
                }

            } catch (IOException e) {
                fail("Invalid serialization.");
                packet.getProcessor().setCancelled(true);
            }

            if (packet.isBlockPlace()) {
                WrappedPacketInBlockPlace wrappedPacketInBlockPlace = new WrappedPacketInBlockPlace(packet.getRawPacket());

                if (wrappedPacketInBlockPlace.getItemStack().isPresent()) {
                    ItemStack is = wrappedPacketInBlockPlace.getItemStack().get();
                    net.minecraft.server.v1_8_R3.ItemStack stack = CraftItemStack.asNMSCopy(is);

                    if (is.getType().toString().contains("BOOK") && CrasherUtils.isInvalidBookTag(stack.getTag())) {
                        fail();
                        packet.getProcessor().setCancelled(true);
                    }
                }
            }
        }

    }
}
