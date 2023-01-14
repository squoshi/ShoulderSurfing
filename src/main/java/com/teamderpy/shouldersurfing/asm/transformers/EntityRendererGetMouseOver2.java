package com.teamderpy.shouldersurfing.asm.transformers;

import static org.objectweb.asm.Opcodes.CHECKCAST;
import static org.objectweb.asm.Opcodes.INVOKEINTERFACE;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

import com.teamderpy.shouldersurfing.asm.Mappings;
import com.teamderpy.shouldersurfing.asm.ShoulderTransformer;

public class EntityRendererGetMouseOver2 extends ShoulderTransformer
{
	@Override
	protected InsnList searchList(Mappings mappings, boolean obf)
	{
		InsnList searchList = new InsnList();
		searchList.add(new MethodInsnNode(INVOKEVIRTUAL, mappings.map("AxisAlignedBB", obf), mappings.map("AxisAlignedBB#calculateIntercept", obf), mappings.desc("AxisAlignedBB#calculateIntercept", obf), false));
		return searchList;
	}
	
	@Override
	public void transform(Mappings mappings, boolean obf, MethodNode method, int offset)
	{
		// RayTraceResult raytraceresult = axisalignedbb.calculateIntercept(vec3, vec32);
		// ->
		// RayTraceResult raytraceresult = axisalignedbb.calculateIntercept(vec32.getKey(), vec32.getValue());
		
		AbstractInsnNode vec32 = method.instructions.get(offset - 1);
		
		if(vec32 instanceof VarInsnNode)
		{
			method.instructions.set(vec32.getPrevious(), new VarInsnNode(vec32.getOpcode(), ((VarInsnNode) vec32).var));
			method.instructions.insertBefore(vec32, new MethodInsnNode(INVOKEINTERFACE, "java/util/Map$Entry", "getKey", "()Ljava/lang/Object;", true));
			method.instructions.insertBefore(vec32, new TypeInsnNode(CHECKCAST, mappings.map("Vec3d", obf)));
			method.instructions.insert(vec32, new TypeInsnNode(CHECKCAST, mappings.map("Vec3d", obf)));
			method.instructions.insert(vec32, new MethodInsnNode(INVOKEINTERFACE, "java/util/Map$Entry", "getValue", "()Ljava/lang/Object;", true));
		}
	}
	
	@Override
	public String getClassId()
	{
		return "EntityRenderer";
	}
	
	@Override
	public String getMethodId()
	{
		return "EntityRenderer#getMouseOver";
	}
	
	@Override
	protected boolean hasMethodTransformer()
	{
		return true;
	}
	
	@Override
	protected boolean hasClassTransformer()
	{
		return false;
	}
}