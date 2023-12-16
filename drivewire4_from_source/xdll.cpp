//--------------------------------------------------------------------------------------------------
//xdll.cpp
//--------------------------------------------------------------------------------------------------

// compile with g++ -o libx.so -shared -fPIC -Wl,-soname,libx.so -L/usr/lib/X11 -I/usr/include/X11 xdll.cpp -lX11

#include <Xlib.h>
#include <stdio.h>

class a{
public:
  a() { XInitThreads(); }
};

a X;


