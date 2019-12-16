package fr.raksrinana.itempiping;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.*;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

class SidedProxy{
	SidedProxy(){
		FMLJavaModLoadingContext.get().getModEventBus().addListener(SidedProxy::commonSetup);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(SidedProxy::enqueueIMC);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(SidedProxy::processIMC);
		MinecraftForge.EVENT_BUS.register(this);
	}
	
	private static void commonSetup(FMLCommonSetupEvent event){
	}
	
	private static void enqueueIMC(final InterModEnqueueEvent event){
	}
	
	private static void processIMC(final InterModProcessEvent event){
	}
	
	@SubscribeEvent
	public void serverStarting(FMLServerStartingEvent event){
	}
	
	static class Client extends SidedProxy{
		Client(){
			FMLJavaModLoadingContext.get().getModEventBus().addListener(Client::clientSetup);
		}
		
		private static void clientSetup(FMLClientSetupEvent event){
		}
	}
	
	static class Server extends SidedProxy{
		Server(){
			FMLJavaModLoadingContext.get().getModEventBus().addListener(Server::serverSetup);
		}
		
		private static void serverSetup(FMLDedicatedServerSetupEvent event){
		}
	}
}
