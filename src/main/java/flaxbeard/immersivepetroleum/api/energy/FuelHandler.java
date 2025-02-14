package flaxbeard.immersivepetroleum.api.energy;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import flaxbeard.immersivepetroleum.ImmersivePetroleum;
import flaxbeard.immersivepetroleum.common.cfg.ConfigUtils;
import flaxbeard.immersivepetroleum.common.cfg.IPServerConfig;
import flaxbeard.immersivepetroleum.common.util.RegistryUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fml.event.config.ModConfigEvent;

public class FuelHandler{
	protected static final Logger log = LogManager.getLogger(ImmersivePetroleum.MODID + "/FuelHandler");
	
	static final Map<ResourceLocation, Values> portablegen = new HashMap<>();
	
	static final Map<ResourceLocation, Integer> motorboatAmountTick = new HashMap<>();
	
	public static void registerPortableGeneratorFuel(Fluid fuel, int fluxPerTick, int mbPerTick){
		if(fuel != null){
			registerPortableGeneratorFuel(RegistryUtils.getRegistryNameOf(fuel), mbPerTick, fluxPerTick);
		}
	}
	
	public static void registerMotorboatFuel(Fluid fuel, int mbPerTick){
		if(fuel != null){
			registerMotorboatFuel(RegistryUtils.getRegistryNameOf(fuel), mbPerTick);
		}
	}
	
	public static void registerPortableGeneratorFuel(ResourceLocation fuelRL, int fluxPerTick, int mbPerTick){
		if(fuelRL != null && !fuelRL.toString().isEmpty()){
			portablegen.put(fuelRL, new Values(fluxPerTick, mbPerTick));
			
			log.info("Added {} as Portable Generator Fuel. ({}RF/t {}mB/t)", fuelRL, fluxPerTick, mbPerTick);
		}
	}
	
	public static void registerMotorboatFuel(ResourceLocation fuelRL, int mbPerTick){
		if(fuelRL != null && !fuelRL.toString().isEmpty()){
			motorboatAmountTick.put(fuelRL, mbPerTick);
			
			log.info("Added {} as Motorboat Fuel. ({} mB/t)", fuelRL, mbPerTick);
		}
	}
	
	public static boolean isValidBoatFuel(Fluid fuel){
		return fuel != null && motorboatAmountTick.containsKey(RegistryUtils.getRegistryNameOf(fuel));
	}
	
	public static boolean isValidFuel(Fluid fuel){
		return fuel != null && portablegen.containsKey(RegistryUtils.getRegistryNameOf(fuel));
	}
	
	public static int getBoatFuelUse(Fluid fuel){
		if(!isValidBoatFuel(fuel))
			return 0;
		
		return motorboatAmountTick.get(RegistryUtils.getRegistryNameOf(fuel));
	}
	
	public static int getGeneratorFuelUse(Fluid fuel){
		if(!isValidFuel(fuel))
			return 0;
		
		return portablegen.get(RegistryUtils.getRegistryNameOf(fuel)).mBPerConsume;
	}
	
	public static int getFluxGeneratedPerTick(Fluid fuel){
		if(!isValidFuel(fuel))
			return 0;
		
		return portablegen.get(RegistryUtils.getRegistryNameOf(fuel)).fluxPerTick;
	}
	
	public static void onConfigReload(ModConfigEvent ev){
		if(ev.getConfig().getSpec() != IPServerConfig.ALL){
			return;
		}
		
		portablegen.clear();
		motorboatAmountTick.clear();
		
		ConfigUtils.addGeneratorFuel(IPServerConfig.GENERATION.fuels.get());
		ConfigUtils.addBoatFuel(IPServerConfig.MISCELLANEOUS.boat_fuels.get());
	}
	
	private record Values(int fluxPerTick, int mBPerConsume){
	}
}
