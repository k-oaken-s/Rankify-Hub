export const getAnonymousId = (): string => {
  const key = "anonymousId";

  let anonymousId = localStorage.getItem(key);

  if (!anonymousId) {
    anonymousId = generateAnonymousId();
    localStorage.setItem(key, anonymousId);
  }

  return anonymousId;
};

const generateAnonymousId = (): string => {
  return crypto.randomUUID();
};
