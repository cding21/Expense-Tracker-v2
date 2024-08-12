export function validateUsername(value: string) {
  // Check if the username is at least 4 characters long and does not contain any special characters
  if (value.length < 4) {
    return 'Name must have at least 4 letters';
  } else if (!/^[a-zA-Z0-9_]*$/.test(value)) {
    return 'Name must not contain any special characters';
  }
  return null;
}

export function validatePassword(value: string) {
  // Check if the password is at least 8 characters long, must contain at least one lower-case and
  // upper-case letter, one number, and contains at least one special characters
  if (value.length < 8) {
    return 'Password must have at least 8 characters';
  } else if (!/[a-z]/.test(value)) {
    return 'Password must contain at least one lower-case letter';
  } else if (!/[A-Z]/.test(value)) {
    return 'Password must contain at least one upper-case letter';
  } else if (!/[0-9]/.test(value)) {
    return 'Password must contain at least one number';
  } else if (!/[!@#$%^&*]/.test(value)) {
    return 'Password must contain at least one special character';
  }
  return null;
}
