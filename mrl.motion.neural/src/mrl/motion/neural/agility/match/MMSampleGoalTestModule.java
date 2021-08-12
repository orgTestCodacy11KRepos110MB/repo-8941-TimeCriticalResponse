package mrl.motion.neural.agility.match;

import java.util.ArrayList;

import javax.vecmath.Point2d;
import javax.vecmath.Point3d;
import javax.vecmath.Vector2d;

import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;

import mrl.motion.data.FootContactDetection;
import mrl.motion.data.MDatabase;
import mrl.motion.data.Motion;
import mrl.motion.data.MotionData;
import mrl.motion.data.edit.MotionEdit;
import mrl.motion.data.trasf.FootSlipCleanup;
import mrl.motion.data.trasf.MotionTransform;
import mrl.motion.data.trasf.Pose2d;
import mrl.motion.graph.MotionSegment;
import mrl.motion.neural.agility.RotationModel.RotationGoal;
import mrl.motion.neural.agility.AgilityModel;
import mrl.motion.neural.agility.FightModel;
import mrl.motion.neural.agility.JumpModel;
import mrl.motion.neural.agility.MotionMatchingSampling;
import mrl.motion.neural.agility.PokeModel;
import mrl.motion.neural.agility.PositionModel;
import mrl.motion.neural.agility.RotationModel;
import mrl.motion.neural.agility.RotationSpeedModel;
import mrl.motion.neural.agility.TrainingDataGenerator;
import mrl.motion.neural.agility.match.MotionMatching.MatchingPath;
import mrl.motion.position.PositionMotion;
import mrl.motion.viewer.module.MainViewerModule;
import mrl.motion.viewer.module.TimeBasedList;
import mrl.util.Configuration;
import mrl.util.MathUtil;
import mrl.util.Pair;
import mrl.widget.app.Item.ItemDescription;
import mrl.widget.app.ItemListModule;
import mrl.widget.app.MainApplication;
import mrl.widget.app.Module;

public class MMSampleGoalTestModule extends Module {

	private MDatabase database;

	@Override
	protected void initializeImpl() {
		AgilityModel model;
		Configuration.MGRAPH_EDGE_WEIGHT_LIMIT = 300;
		AgilityModel.TIME_EXTENSION_RATIO = 0.1;
		AgilityModel.GOAL_TIME_LIMIT = 30;
		String tPoseFile = "t_pose_actor.bvh";
		String folder;
		
//		RotationMotionMatching.STRAIGHT_MARGIN = 3;
//		JumpModel.rotErrorRatio = 100;
//		JumpModel.USE_STRAIGHT_SAMPLING = true;
//		JumpModel.FIX_LOCOMOTION_AFTER_ACTION = false;
//		JumpModel.LOCOMOTION_RATIO = 0.6;
//		folder = "dc_jump_jog";
//		model = new JumpModel(folder);
		
//		FootContactDetection.footDistPointOffset = new int[]{
//				30, 100, 0
//		};
//		PositionModel.posErrorRatio = 5;
//		PositionModel.BASE_TARGET_LENGTH = 80;
//		folder = "poke_bvh_ver2";
//		tPoseFile = "t_pose_poke.bvh";
//		model = new PositionModel();
		
//		AgilityGoal.TIME_EXTENSION_RATIO = 0.33;
//		RotationModel.GOAL_TIME_LIMIT = 25; 
//		RotationMotionMatching.rotErrorRatio = 100;
//		RotationModel.USE_STRAIGHT_SAMPLING = true;
////		MotionMatching.printLog = true;
//		folder = "dc_jog2";
//		model = new RotationModel();
		
//		folder = "runjogwalk";
//		RotationMotionMatching.STRAIGHT_MARGIN = 1;
//		AgilityGoal.TIME_EXTENSION_RATIO = 0.33;
//		RotationModel.GOAL_TIME_LIMIT = 30; 
//		RotationMotionMatching.rotErrorRatio = 100;
//		RotationModel.USE_STRAIGHT_SAMPLING = true;
//		model = new RotationSpeedModel();
//		
		
		folder = "poke_bvh_ver2";
		tPoseFile = "t_pose_poke.bvh";
		RotationModel.GOAL_TIME_LIMIT = 25; 
		model = new PokeModel(folder);
		
		
		database = TrainingDataGenerator.loadDatabase(folder, tPoseFile);
		MotionMatchingSampling sampling = new MotionMatchingSampling(database, model);
		
		ArrayList<Motion> motionList = sampling.sample(1000);
//		System.out.println("################");
//		MotionTransform.print = true;
//		for (int i = 295; i < 299; i++) {
//			System.out.println(i + " : " + MotionTransform.alignAngle(motionList.get(i).root()));
//			System.out.println(RotationMotionMatching.getRotation(motionList.get(i), motionList.get(i+1)));
//		}
//		System.exit(0);
		
		MainViewerModule viewer = getModule(MainViewerModule.class);
		ItemListModule listModule = getModule(ItemListModule.class);
		TimeBasedList<Pose2d> cPoseList = new TimeBasedList<Pose2d>();
		for (Motion m : motionList) {
			cPoseList.add(PositionMotion.getPose(m));
		}
		listModule.addSingleItem("motion", new MotionData(motionList), ItemDescription.red());
		listModule.addSingleItem("notEdited", new MotionData(sampling.totalNotEdited));
		listModule.addSingleItem("goal", sampling.controlParameterObjects, ItemDescription.red());
		listModule.addSingleItem("cPoseList", cPoseList);
		
	}
	
	public static void main(String[] args) {
		MainApplication.run(new MMSampleGoalTestModule());
	}
}
