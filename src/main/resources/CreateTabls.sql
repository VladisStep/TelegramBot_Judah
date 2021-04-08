DROP TABLE IF EXISTS public.telegramusers;
DROP TABLE IF EXISTS public.userstask;

CREATE TABLE public.telegramusers (
                                      token character varying(50) PRIMARY KEY,
                                      name character varying(255) NULL,
                                      city character varying(255) NULL,
                                      state integer DEFAULT 0,
                                      notifications boolean NULL
);

CREATE TABLE public.userstask (
                                  date character varying(50) NULL,
                                  text character varying(255) NULL,
                                  token character varying(50) NULL,
                                  id SERIAL  PRIMARY KEY
);x


SELECT * FROM public.telegramusers;
SELECT * FROM public.userstask;








