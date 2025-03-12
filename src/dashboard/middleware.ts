import { NextURL } from 'next/dist/server/web/next-url';
import { cookies } from 'next/headers';
import { NextRequest, NextResponse } from 'next/server';

export function middleware(request: NextRequest) {
  const isAuthenticated = cookies().get('token') !== undefined;
  // return !isAuthenticated
  //   ? NextResponse.redirect(new NextURL('/sign-in', request.url))
  //   : NextResponse.next();
  return NextResponse.next();
}

export const config = {
  matcher: ['/((?!api|_next|favicon.svg|sign-in|sign-up|forgot-password).*)'],
};
