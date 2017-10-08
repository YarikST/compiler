#include <stdio.h>
#include <stdlib.h>

using namespace std;
extern "C"{
/*
__attribute__((codcall)) void	log		(char *str);
__attribute__((codcall)) void	log		(int a);
__attribute__((codcall)) void	log		(double a);
__attribute__((codcall)) int 	add		(int a,int b);
__attribute__((codcall)) int 	sub		(int a,int b);
__attribute__((codcall)) int 	mul		(int a,int b);
__attribute__((codcall)) int 	div		(int a,int b);
__attribute__((codcall)) double add		(double a,double b);
__attribute__((codcall)) double sub		(double a,double b);
__attribute__((codcall)) double mul		(double a,double b);
__attribute__((codcall)) double div		(double a,double b);
__attribute__((codcall)) double convert	(int a);
__attribute__((codcall)) int 	convert (double a);
*/
//-----------------------log-------------------
__attribute__((codcall)) void log_str(char *str)
{
}
__attribute__((codcall)) void log_i(int a)
{
}
__attribute__((codcall)) void log_d(double a)
{
}

//--------------operators----------------------
__attribute__((codcall)) int add_i(int a,int b)
{
return a+b;
}

__attribute__((codcall)) int sub_i(int a,int b)
{
return a-b;
}

__attribute__((codcall)) int mul_i(int a,int b)
{
return a*b;
}

__attribute__((codcall)) int div_i(int a,int b)
{
return a/b;
}

__attribute__((codcall)) double add_d(double a,double b)
{
return a+b;
}

__attribute__((codcall)) double sub_d(double a,double b)
{
return a-b;
}

__attribute__((codcall)) double mul_d(double a,double b)
{
return a*b;
}

__attribute__((codcall)) double div_d(double a,double b)
{
return a/b;
}
//---------------------convert-----------------------------

__attribute__((codcall)) float i_f(int a)
{
return a;
}
__attribute__((codcall)) int f_i(float a)
{
return a;
}
__attribute__((codcall)) int c_i(char a)
{
return a;
}
__attribute__((codcall)) char i_c(int a)
{
return a;
}
//--------------------in---------------------------------
__attribute__((codcall)) float in_d()
{
float temp=0;
scanf("%f", &temp);
return temp;
}
__attribute__((codcall)) int in_i()
{
int temp=0;
scanf("%i", &temp);
return temp;
}
__attribute__((codcall)) int in_c()
{
char temp=0;
//scanf("%c", &temp);
scanf("%1s",&temp); 
return temp;
}
__attribute__((codcall)) int in_s()
{
int temp=0;
scanf("%i", &temp);
return temp;
}

//
/*
double temp;
__attribute__((codcall)) double test()
{
temp=5.6;
int temp1 = 123,temp2=456,temp3;
temp3=temp1+temp2;
double a=1.2;
double b=3.4;
double c = add_d(a,b);
return c;
}
*/
__attribute__((codcall)) double test()
{
double a=1.2;
double b=3.4;
double c = add_d(a,b);
return c;
}

__attribute__((codcall)) float test2()
{
float temp=0;
scanf("%f", &temp);
return temp;
}
//
//---------------------memmory-----------------------------
/*
__attribute__((codcall)) void log()
{
	char str[32*2];
    sprintf(str, "%d", a);
    printf(str);
	
   int radix = 10;  //система счисления
   char buffer[32*2]; //результат
   char *p;  //указатель на результат
   p = itoa(a,buffer,radix);
}
*/
}

