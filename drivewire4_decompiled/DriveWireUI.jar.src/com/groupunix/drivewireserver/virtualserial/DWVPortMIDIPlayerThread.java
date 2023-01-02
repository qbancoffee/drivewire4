/*     */ package com.groupunix.drivewireserver.virtualserial;
/*     */ 
/*     */ import com.groupunix.drivewireserver.dwexceptions.DWPortNotValidException;
/*     */ import com.groupunix.drivewireserver.dwprotocolhandler.DWProtocolHandler;
/*     */ import java.io.IOException;
/*     */ import javax.sound.midi.InvalidMidiDataException;
/*     */ import javax.sound.midi.MidiSystem;
/*     */ import javax.sound.midi.MidiUnavailableException;
/*     */ import javax.sound.midi.Sequence;
/*     */ import javax.sound.midi.Sequencer;
/*     */ import javax.sound.midi.Transmitter;
/*     */ import org.apache.log4j.Logger;
/*     */ 
/*     */ 
/*     */ 
/*     */ public class DWVPortMIDIPlayerThread
/*     */   implements Runnable
/*     */ {
/*  19 */   private static final Logger logger = Logger.getLogger("DWServer.DWVPortMIDIPlayerThread");
/*     */   
/*  21 */   private int vport = -1;
/*  22 */   private DWVSerialCircularBuffer inputBuffer = new DWVSerialCircularBuffer(1024, true);
/*  23 */   private static Sequencer sm_sequencer = null;
/*     */   
/*     */   private DWProtocolHandler dwProto;
/*     */ 
/*     */   
/*     */   public DWVPortMIDIPlayerThread(DWProtocolHandler dwProto, int vport, DWVSerialCircularBuffer inputBuffer) {
/*  29 */     this.vport = vport;
/*  30 */     this.inputBuffer = inputBuffer;
/*  31 */     this.dwProto = dwProto;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void run() {
/*  37 */     Thread.currentThread().setName("midiplay-" + Thread.currentThread().getId());
/*  38 */     Thread.currentThread().setPriority(5);
/*     */     
/*  40 */     logger.debug("run");
/*     */ 
/*     */     
/*  43 */     Sequence sequence = null;
/*     */ 
/*     */ 
/*     */     
/*     */     try {
/*  48 */       sequence = MidiSystem.getSequence(this.inputBuffer.getInputStream());
/*     */     }
/*  50 */     catch (InvalidMidiDataException e) {
/*     */       
/*  52 */       logger.warn(e.getMessage());
/*     */     }
/*  54 */     catch (IOException e) {
/*     */       
/*  56 */       logger.warn(e.getMessage());
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/*     */     try {
/*  62 */       sm_sequencer = MidiSystem.getSequencer(false);
/*     */     
/*     */     }
/*  65 */     catch (MidiUnavailableException e) {
/*     */       
/*  67 */       logger.warn(e.getMessage());
/*     */     } 
/*     */ 
/*     */     
/*  71 */     if (sm_sequencer == null)
/*     */     {
/*  73 */       logger.error("can't get a Sequencer");
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     try {
/*  80 */       sm_sequencer.open();
/*     */     }
/*  82 */     catch (MidiUnavailableException e) {
/*     */       
/*  84 */       logger.warn(e.getMessage());
/*     */     } 
/*     */ 
/*     */     
/*     */     try {
/*  89 */       sm_sequencer.setSequence(sequence);
/*     */     }
/*  91 */     catch (InvalidMidiDataException e) {
/*     */       
/*  93 */       logger.warn(e.getMessage());
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     try {
/* 102 */       Transmitter seqTransmitter = sm_sequencer.getTransmitter();
/* 103 */       seqTransmitter.setReceiver(this.dwProto.getVPorts().getMidiReceiver());
/*     */     }
/* 105 */     catch (MidiUnavailableException e) {
/*     */       
/* 107 */       logger.warn(e.getMessage());
/*     */     } 
/*     */     
/* 110 */     sm_sequencer.start();
/*     */ 
/*     */ 
/*     */     
/*     */     try {
/* 115 */       this.dwProto.getVPorts().closePort(this.vport);
/*     */     }
/* 117 */     catch (DWPortNotValidException e) {
/*     */       
/* 119 */       logger.warn(e.getMessage());
/*     */     } 
/*     */     
/* 122 */     logger.debug("exit");
/*     */   }
/*     */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireserver/virtualserial/DWVPortMIDIPlayerThread.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */