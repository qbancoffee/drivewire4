/*     */ package com.groupunix.drivewireserver.virtualserial;
/*     */ 
/*     */ import com.Ostermiller.util.BufferOverflowException;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import org.apache.log4j.Logger;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class DWVSerialCircularBuffer
/*     */ {
/*     */   private static final int DEFAULT_SIZE = 1024;
/*     */   public static final int INFINITE_SIZE = -1;
/*  67 */   private byte DW_PD_INT = 0;
/*  68 */   private byte DW_PD_QUT = 0;
/*     */ 
/*     */ 
/*     */   
/*  72 */   private static final Logger logger = Logger.getLogger("DWServer.DWVSerialCircularBuffer");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected byte[] buffer;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 102 */   protected volatile int readPosition = 0;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 108 */   protected volatile int writePosition = 0;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 114 */   protected volatile int markPosition = 0;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 121 */   protected volatile int markSize = 0;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected volatile boolean infinite = false;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected boolean blockingWrite = true;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 140 */   protected InputStream in = new CircularByteBufferInputStream();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected boolean inputStreamClosed = false;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 152 */   protected OutputStream out = new CircularByteBufferOutputStream();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected boolean outputStreamClosed = false;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void clear() {
/* 168 */     synchronized (this) {
/* 169 */       this.readPosition = 0;
/* 170 */       this.writePosition = 0;
/* 171 */       this.markPosition = 0;
/* 172 */       this.outputStreamClosed = false;
/* 173 */       this.inputStreamClosed = false;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public OutputStream getOutputStream() {
/* 193 */     return this.out;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public InputStream getInputStream() {
/* 208 */     return this.in;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getAvailable() {
/* 224 */     synchronized (this) {
/* 225 */       return available();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getSpaceLeft() {
/* 243 */     synchronized (this) {
/* 244 */       return spaceLeft();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getSize() {
/* 261 */     synchronized (this) {
/* 262 */       return this.buffer.length;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void resize() {
/* 272 */     byte[] newBuffer = new byte[this.buffer.length * 2];
/* 273 */     int marked = marked();
/* 274 */     int available = available();
/* 275 */     if (this.markPosition <= this.writePosition) {
/*     */ 
/*     */ 
/*     */       
/* 279 */       int length = this.writePosition - this.markPosition;
/* 280 */       System.arraycopy(this.buffer, this.markPosition, newBuffer, 0, length);
/*     */     } else {
/* 282 */       int length1 = this.buffer.length - this.markPosition;
/* 283 */       System.arraycopy(this.buffer, this.markPosition, newBuffer, 0, length1);
/* 284 */       int length2 = this.writePosition;
/* 285 */       System.arraycopy(this.buffer, 0, newBuffer, length1, length2);
/*     */     } 
/* 287 */     this.buffer = newBuffer;
/* 288 */     this.markPosition = 0;
/* 289 */     this.readPosition = marked;
/* 290 */     this.writePosition = marked + available;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private int spaceLeft() {
/* 299 */     if (this.writePosition < this.markPosition)
/*     */     {
/*     */ 
/*     */       
/* 303 */       return this.markPosition - this.writePosition - 1;
/*     */     }
/*     */     
/* 306 */     return this.buffer.length - 1 - this.writePosition - this.markPosition;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private int available() {
/* 319 */     if (this.readPosition <= this.writePosition)
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 342 */       return this.writePosition - this.readPosition;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 377 */     return this.buffer.length - this.readPosition - this.writePosition;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private int marked() {
/* 390 */     if (this.markPosition <= this.readPosition)
/*     */     {
/*     */ 
/*     */       
/* 394 */       return this.readPosition - this.markPosition;
/*     */     }
/*     */     
/* 397 */     return this.buffer.length - this.markPosition - this.readPosition;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void ensureMark() {
/* 407 */     if (marked() >= this.markSize) {
/* 408 */       this.markPosition = this.readPosition;
/* 409 */       this.markSize = 0;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public DWVSerialCircularBuffer() {
/* 421 */     this(1024, true);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public DWVSerialCircularBuffer(int size) {
/* 442 */     this(size, true);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public DWVSerialCircularBuffer(boolean blockingWrite) {
/* 456 */     this(1024, blockingWrite);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public DWVSerialCircularBuffer(int size, boolean blockingWrite) {
/* 479 */     if (size == -1) {
/* 480 */       this.buffer = new byte[1024];
/* 481 */       this.infinite = true;
/*     */     } else {
/* 483 */       this.buffer = new byte[size];
/* 484 */       this.infinite = false;
/*     */     } 
/* 486 */     this.blockingWrite = blockingWrite;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void setDW_PD_INT(byte dW_PD_INT) {
/* 492 */     this.DW_PD_INT = dW_PD_INT;
/*     */   }
/*     */   
/*     */   public int getDW_PD_INT() {
/* 496 */     return this.DW_PD_INT;
/*     */   }
/*     */   
/*     */   public void setDW_PD_QUT(byte dW_PD_QUT) {
/* 500 */     this.DW_PD_QUT = dW_PD_QUT;
/*     */   }
/*     */   
/*     */   public int getDW_PD_QUT() {
/* 504 */     return this.DW_PD_QUT;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected class CircularByteBufferInputStream
/*     */     extends InputStream
/*     */   {
/*     */     public int available() throws IOException {
/* 525 */       synchronized (DWVSerialCircularBuffer.this) {
/* 526 */         if (DWVSerialCircularBuffer.this.inputStreamClosed) throw new IOException("InputStream has been closed, it is not ready."); 
/* 527 */         return DWVSerialCircularBuffer.this.available();
/*     */       } 
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public void close() throws IOException {
/* 541 */       synchronized (DWVSerialCircularBuffer.this) {
/* 542 */         DWVSerialCircularBuffer.this.inputStreamClosed = true;
/*     */       } 
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public void mark(int readAheadLimit) {
/* 560 */       synchronized (DWVSerialCircularBuffer.this) {
/*     */         
/* 562 */         if (DWVSerialCircularBuffer.this.buffer.length - 1 > readAheadLimit) {
/* 563 */           DWVSerialCircularBuffer.this.markSize = readAheadLimit;
/* 564 */           DWVSerialCircularBuffer.this.markPosition = DWVSerialCircularBuffer.this.readPosition;
/*     */         } 
/*     */       } 
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public boolean markSupported() {
/* 577 */       return true;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public int read() throws IOException {
/*     */       while (true) {
/* 593 */         synchronized (DWVSerialCircularBuffer.this) {
/* 594 */           if (DWVSerialCircularBuffer.this.inputStreamClosed) throw new IOException("InputStream has been closed; cannot read from a closed InputStream."); 
/* 595 */           int available = DWVSerialCircularBuffer.this.available();
/* 596 */           if (available > 0) {
/* 597 */             int result = DWVSerialCircularBuffer.this.buffer[DWVSerialCircularBuffer.this.readPosition] & 0xFF;
/* 598 */             DWVSerialCircularBuffer.this.readPosition++;
/* 599 */             if (DWVSerialCircularBuffer.this.readPosition == DWVSerialCircularBuffer.this.buffer.length) {
/* 600 */               DWVSerialCircularBuffer.this.readPosition = 0;
/*     */             }
/* 602 */             DWVSerialCircularBuffer.this.ensureMark();
/* 603 */             return result;
/* 604 */           }  if (DWVSerialCircularBuffer.this.outputStreamClosed) {
/* 605 */             return -1;
/*     */           }
/*     */         } 
/*     */         try {
/* 609 */           Thread.sleep(100L);
/* 610 */         } catch (Exception x) {
/* 611 */           throw new IOException("Blocking read operation interrupted.");
/*     */         } 
/*     */       } 
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public int read(byte[] cbuf) throws IOException {
/* 629 */       return read(cbuf, 0, cbuf.length);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public int read(byte[] cbuf, int off, int len) throws IOException {
/*     */       while (true) {
/* 648 */         synchronized (DWVSerialCircularBuffer.this) {
/* 649 */           if (DWVSerialCircularBuffer.this.inputStreamClosed) throw new IOException("InputStream has been closed; cannot read from a closed InputStream."); 
/* 650 */           int available = DWVSerialCircularBuffer.this.available();
/* 651 */           if (available > 0) {
/* 652 */             int length = Math.min(len, available);
/* 653 */             int firstLen = Math.min(length, DWVSerialCircularBuffer.this.buffer.length - DWVSerialCircularBuffer.this.readPosition);
/* 654 */             int secondLen = length - firstLen;
/* 655 */             System.arraycopy(DWVSerialCircularBuffer.this.buffer, DWVSerialCircularBuffer.this.readPosition, cbuf, off, firstLen);
/* 656 */             if (secondLen > 0) {
/* 657 */               System.arraycopy(DWVSerialCircularBuffer.this.buffer, 0, cbuf, off + firstLen, secondLen);
/* 658 */               DWVSerialCircularBuffer.this.readPosition = secondLen;
/*     */             } else {
/* 660 */               DWVSerialCircularBuffer.this.readPosition += length;
/*     */             } 
/* 662 */             if (DWVSerialCircularBuffer.this.readPosition == DWVSerialCircularBuffer.this.buffer.length) {
/* 663 */               DWVSerialCircularBuffer.this.readPosition = 0;
/*     */             }
/* 665 */             DWVSerialCircularBuffer.this.ensureMark();
/* 666 */             return length;
/* 667 */           }  if (DWVSerialCircularBuffer.this.outputStreamClosed) {
/* 668 */             return -1;
/*     */           }
/*     */         } 
/*     */         try {
/* 672 */           Thread.sleep(100L);
/* 673 */         } catch (Exception x) {
/* 674 */           throw new IOException("Blocking read operation interrupted.");
/*     */         } 
/*     */       } 
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public void reset() throws IOException {
/* 690 */       synchronized (DWVSerialCircularBuffer.this) {
/* 691 */         if (DWVSerialCircularBuffer.this.inputStreamClosed) throw new IOException("InputStream has been closed; cannot reset a closed InputStream."); 
/* 692 */         DWVSerialCircularBuffer.this.readPosition = DWVSerialCircularBuffer.this.markPosition;
/*     */       } 
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public long skip(long n) throws IOException, IllegalArgumentException {
/*     */       while (true) {
/* 710 */         synchronized (DWVSerialCircularBuffer.this) {
/* 711 */           if (DWVSerialCircularBuffer.this.inputStreamClosed) throw new IOException("InputStream has been closed; cannot skip bytes on a closed InputStream."); 
/* 712 */           int available = DWVSerialCircularBuffer.this.available();
/* 713 */           if (available > 0) {
/* 714 */             int length = Math.min((int)n, available);
/* 715 */             int firstLen = Math.min(length, DWVSerialCircularBuffer.this.buffer.length - DWVSerialCircularBuffer.this.readPosition);
/* 716 */             int secondLen = length - firstLen;
/* 717 */             if (secondLen > 0) {
/* 718 */               DWVSerialCircularBuffer.this.readPosition = secondLen;
/*     */             } else {
/* 720 */               DWVSerialCircularBuffer.this.readPosition += length;
/*     */             } 
/* 722 */             if (DWVSerialCircularBuffer.this.readPosition == DWVSerialCircularBuffer.this.buffer.length) {
/* 723 */               DWVSerialCircularBuffer.this.readPosition = 0;
/*     */             }
/* 725 */             DWVSerialCircularBuffer.this.ensureMark();
/* 726 */             return length;
/* 727 */           }  if (DWVSerialCircularBuffer.this.outputStreamClosed) {
/* 728 */             return 0L;
/*     */           }
/*     */         } 
/*     */         try {
/* 732 */           Thread.sleep(100L);
/* 733 */         } catch (Exception x) {
/* 734 */           throw new IOException("Blocking read operation interrupted.");
/*     */         } 
/*     */       } 
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected class CircularByteBufferOutputStream
/*     */     extends OutputStream
/*     */   {
/*     */     public void close() throws IOException {
/* 763 */       synchronized (DWVSerialCircularBuffer.this) {
/* 764 */         if (!DWVSerialCircularBuffer.this.outputStreamClosed) {
/* 765 */           flush();
/*     */         }
/* 767 */         DWVSerialCircularBuffer.this.outputStreamClosed = true;
/*     */       } 
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public void flush() throws IOException {
/* 779 */       if (DWVSerialCircularBuffer.this.outputStreamClosed) throw new IOException("OutputStream has been closed; cannot flush a closed OutputStream."); 
/* 780 */       if (DWVSerialCircularBuffer.this.inputStreamClosed) throw new IOException("Buffer closed by inputStream; cannot flush.");
/*     */     
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public void write(byte[] cbuf) throws IOException {
/* 798 */       write(cbuf, 0, cbuf.length);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public void write(byte[] cbuf, int off, int len) throws IOException {
/* 817 */       while (len > 0) {
/* 818 */         synchronized (DWVSerialCircularBuffer.this) {
/* 819 */           if (DWVSerialCircularBuffer.this.outputStreamClosed) throw new IOException("OutputStream has been closed; cannot write to a closed OutputStream."); 
/* 820 */           if (DWVSerialCircularBuffer.this.inputStreamClosed) throw new IOException("Buffer closed by InputStream; cannot write to a closed buffer."); 
/* 821 */           int spaceLeft = DWVSerialCircularBuffer.this.spaceLeft();
/* 822 */           while (DWVSerialCircularBuffer.this.infinite && spaceLeft < len) {
/* 823 */             DWVSerialCircularBuffer.this.resize();
/* 824 */             spaceLeft = DWVSerialCircularBuffer.this.spaceLeft();
/*     */           } 
/* 826 */           if (!DWVSerialCircularBuffer.this.blockingWrite && spaceLeft < len) throw new BufferOverflowException("CircularByteBuffer is full; cannot write " + len + " bytes"); 
/* 827 */           int realLen = Math.min(len, spaceLeft);
/* 828 */           int firstLen = Math.min(realLen, DWVSerialCircularBuffer.this.buffer.length - DWVSerialCircularBuffer.this.writePosition);
/* 829 */           int secondLen = Math.min(realLen - firstLen, DWVSerialCircularBuffer.this.buffer.length - DWVSerialCircularBuffer.this.markPosition - 1);
/* 830 */           int written = firstLen + secondLen;
/* 831 */           if (firstLen > 0) {
/* 832 */             System.arraycopy(cbuf, off, DWVSerialCircularBuffer.this.buffer, DWVSerialCircularBuffer.this.writePosition, firstLen);
/*     */           }
/* 834 */           if (secondLen > 0) {
/* 835 */             System.arraycopy(cbuf, off + firstLen, DWVSerialCircularBuffer.this.buffer, 0, secondLen);
/* 836 */             DWVSerialCircularBuffer.this.writePosition = secondLen;
/*     */           } else {
/* 838 */             DWVSerialCircularBuffer.this.writePosition += written;
/*     */           } 
/* 840 */           if (DWVSerialCircularBuffer.this.writePosition == DWVSerialCircularBuffer.this.buffer.length) {
/* 841 */             DWVSerialCircularBuffer.this.writePosition = 0;
/*     */           }
/* 843 */           off += written;
/* 844 */           len -= written;
/*     */         } 
/* 846 */         if (len > 0) {
/*     */           try {
/* 848 */             Thread.sleep(100L);
/* 849 */           } catch (Exception x) {
/* 850 */             throw new IOException("Waiting for available space in buffer interrupted.");
/*     */           } 
/*     */         }
/*     */       } 
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public void write(int c) throws IOException {
/* 871 */       boolean written = false;
/* 872 */       while (!written) {
/* 873 */         synchronized (DWVSerialCircularBuffer.this) {
/* 874 */           if (DWVSerialCircularBuffer.this.outputStreamClosed) throw new IOException("OutputStream has been closed; cannot write to a closed OutputStream."); 
/* 875 */           if (DWVSerialCircularBuffer.this.inputStreamClosed) throw new IOException("Buffer closed by InputStream; cannot write to a closed buffer."); 
/* 876 */           int spaceLeft = DWVSerialCircularBuffer.this.spaceLeft();
/* 877 */           while (DWVSerialCircularBuffer.this.infinite && spaceLeft < 1) {
/* 878 */             DWVSerialCircularBuffer.this.resize();
/* 879 */             spaceLeft = DWVSerialCircularBuffer.this.spaceLeft();
/*     */           } 
/* 881 */           if (!DWVSerialCircularBuffer.this.blockingWrite && spaceLeft < 1) throw new BufferOverflowException("CircularByteBuffer is full; cannot write 1 byte"); 
/* 882 */           if (spaceLeft > 0) {
/* 883 */             DWVSerialCircularBuffer.this.buffer[DWVSerialCircularBuffer.this.writePosition] = (byte)(c & 0xFF);
/* 884 */             DWVSerialCircularBuffer.this.writePosition++;
/* 885 */             if (DWVSerialCircularBuffer.this.writePosition == DWVSerialCircularBuffer.this.buffer.length) {
/* 886 */               DWVSerialCircularBuffer.this.writePosition = 0;
/*     */             }
/* 888 */             written = true;
/*     */           } 
/*     */         } 
/* 891 */         if (!written)
/*     */           try {
/* 893 */             Thread.sleep(100L);
/* 894 */           } catch (Exception x) {
/* 895 */             throw new IOException("Waiting for available space in buffer interrupted.");
/*     */           }  
/*     */       } 
/*     */     }
/*     */   }
/*     */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireserver/virtualserial/DWVSerialCircularBuffer.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */