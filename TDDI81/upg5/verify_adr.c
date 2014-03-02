#include <stdlib.h>
#include <stdint.h>
#include <stdio.h>
#include "pagedir.h"


/* Kontrollera alla adresser fr�n och med start till och inte med
 * (start+length). */
bool verify_fix_length(void* start, int length)
{
  // ADD YOUR CODE HERE
  char* temp = start;
  char* end = temp + length;

  for(; (char*) pg_round_down( (void*)temp ) < end; temp = (char*)pg_round_down(temp+PGSIZE) )
  {
    if( pagedir_get_page(NULL, (void*)temp ) == NULL )
      return false; 
  } 
  return true;
}

/* Kontrollera alla adresser fr�n och med start till och med den
 * adress som f�rst ineh�ller ett noll-tecken, `\0'. (C-str�ngar
 * lagras p� detta s�tt.) */
bool verify_variable_length(char* start)
{
  // ADD YOUR CODE HERE
  int i = 0;
  for(; pagedir_get_page(NULL, (void*)start) != NULL; ++start ) 
    { 
      printf( "%i %s", i , "\n");
      if( is_end_of_string( start ) )
	return true;
      ++i;
    }
  return false;
}

/* Definition av testfall */
struct test_case_t
{
  void* start;
  unsigned length;
};

#define TEST_CASE_COUNT 6

const struct test_case_t test_case[TEST_CASE_COUNT] =
{
  {(void*)100, 100}, /* one full page */
  {(void*)199, 102},
  {(void*)101, 98},
  {(void*)250, 190},
  {(void*)250, 200},
  {(void*)250, 210}
};

/* Huvudprogrammet utv�rderar din l�sning. */
int main()
{
  int i;
  bool result;
  
  /* Testa algoritmen med ett givet intervall (en buffer). */
  for (i = 0; i < TEST_CASE_COUNT; ++i)
  {
    //printf("Hej hej \n");
    start_evaluate_algorithm(test_case[i].start, test_case[i].length);
    result = verify_fix_length(test_case[i].start, test_case[i].length);
    evaluate(result);
    end_evaluate_algorithm();
  }
  
  /* Testa algoritmen med en str�ng. */
   for (i = 0; i < TEST_CASE_COUNT; ++i)
  {
    //printf("feffefe  \n");
    start_evaluate_algorithm(test_case[i].start, test_case[i].length);
    result = verify_variable_length(test_case[i].start);
    evaluate(result);    
    end_evaluate_algorithm();
  }
  return 0;
}
