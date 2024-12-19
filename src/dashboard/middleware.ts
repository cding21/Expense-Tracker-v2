import { NextURL } from 'next/dist/server/web/next-url';
import { cookies } from 'next/headers';
import { NextRequest, NextResponse } from 'next/server';

export async function middleware(request: NextRequest) {
  const isAuthenticated = (await cookies()).get('token') !== undefined;

  if (isAuthenticated && 
    (request.nextUrl.pathname === '/sign-in' 
      || request.nextUrl.pathname === '/sign-up' 
      || request.nextUrl.pathname === '/forgot-password')
    ) {
    return NextResponse.redirect(new NextURL('/', request.url));
  }

  return !isAuthenticated
    ? NextResponse.redirect(new NextURL('/sign-in', request.url))
    : NextResponse.next();
}

export const config = {
  matcher: ['/((?!api|_next|favicon.svg).*)'],
};
